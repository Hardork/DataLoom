package com.hwq.dataloom.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.manager.AiManager;
import com.hwq.dataloom.model.dto.ai.AskAIWithDataTablesAndFieldsRequest;
import com.hwq.dataloom.model.dto.ai.ChatForSQLRequest;
import com.hwq.dataloom.model.entity.*;
import com.hwq.dataloom.model.enums.ChatHistoryRoleEnum;
import com.hwq.dataloom.model.enums.ChatHistoryStatusEnum;
import com.hwq.dataloom.model.vo.data.QueryAICustomSQLVO;
import com.hwq.dataloom.service.*;
import com.hwq.dataloom.utils.datasource.DatasourceEngine;
import com.hwq.dataloom.websocket.AskSQLWebSocket;
import com.hwq.dataloom.websocket.constants.MessageStatusEnum;
import com.hwq.dataloom.websocket.vo.AskSQLWebSocketMsgVO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.hwq.dataloom.constant.PromptConstants.AI_GEN_CHART;
import static com.hwq.dataloom.constant.PromptConstants.SQL_ANALYSIS_PROMPT;
import static com.hwq.dataloom.constant.UserChatForSQLConstant.*;

/**
 * @author HWQ
 * @date 2024/9/7 16:54
 * @description
 */
@Component
@Slf4j
public class AIServiceImpl implements AIService {


    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private DatasourceEngine datasourceEngine;

    @Resource
    private ChatService chatService;

    @Resource
    private AiManager aiManager;

    @Resource
    private CoreDatasetTableFieldService coreDatasetTableFieldService;

    @Resource
    private CoreDatasourceService coreDatasourceService;

    @Resource
    private AskSQLWebSocket askSQLWebSocket;


    @Override
    public void userChatForSQL(ChatForSQLRequest chatForSQLRequest, User loginUser) {
        Long chatId = chatForSQLRequest.getChatId();
        String question = chatForSQLRequest.getQuestion();
        // 1. 获取模型ID
        Chat chat = chatService.getById(chatId);
        ThrowUtils.throwIf(chat == null, ErrorCode.PARAMS_ERROR, "不存在该助手");
        // 2. 持久化用户消息
        ChatHistory chatHistory = saveChatHistory(ChatHistoryRoleEnum.USER, chatId, chat, question);
        // 3. 获取数据源所有的元数据
        // TODO: 发送分析数据源完毕
        Long datasourceId = chat.getDatasourceId();
        List<AskAIWithDataTablesAndFieldsRequest> dataTablesAndFieldsRequests = getAskAIWithDataTablesAndFieldsRequests(loginUser, datasourceId);
        // 4. 构造请求AI的输入
        String input = buildAskAISQLInput(dataTablesAndFieldsRequests, question);
        log.info("智能问数 消息ID： {}  AI输入: {}", chatHistory.getId(), input);
        // 5. 利用webSocket发送消息通知开始
        AskSQLWebSocketMsgVO askSQLWebSocketMsgVO = new AskSQLWebSocketMsgVO();
        askSQLWebSocketMsgVO.setType(MessageStatusEnum.START.getStatus());
        askSQLWebSocket.sendOneMessage(loginUser.getId(), askSQLWebSocketMsgVO);
        // 6. 询问AI，获取返回的SQL
        String prompt = String.format(SQL_ANALYSIS_PROMPT, 200);
        // TODO：发送提取关联表完毕（显示出关联的表（点击可跳转））
        String sql = aiManager.doChatWithKimi32K(input, prompt);
        try {
            // 7. 执行SQL，并得到返回的结果
            QueryAICustomSQLVO queryAICustomSQLVO = getQueryAICustomSQLVO(loginUser, datasourceId, sql, chatId, chat);
            // TODO: 判断当前的数据大小，如果太大只存SQL，不将结果存入数据库
            List<Map<String, Object>> dataList = queryAICustomSQLVO.getRes();
            if (dataList.size() < 20) {
                // 8. 将查询的结果存放在数据库中
                saveChatHistory(ChatHistoryRoleEnum.MODEL, chatId, chat, JSONUtil.toJsonStr(queryAICustomSQLVO));
            } else {
                // TODO：只存储SQL，后续查询通过SQL进行查询
                saveChatHistory(ChatHistoryRoleEnum.MODEL, chatId, chat, JSONUtil.toJsonStr(queryAICustomSQLVO));
            }
            // 9. 利用webSocket发送消息通知
            AskSQLWebSocketMsgVO res = AskSQLWebSocketMsgVO.builder()
                    .res(queryAICustomSQLVO.getRes())
                    .columns(queryAICustomSQLVO.getColumns())
                    .type(MessageStatusEnum.RUNNING.getStatus())
                    .sql(sql)
                    .build();
            askSQLWebSocket.sendOneMessage(loginUser.getId(), res);
        } catch (Exception e) {
            if (e instanceof SQLException) { // 记录异常
                QueryAICustomSQLVO queryAICustomSQLVO = new QueryAICustomSQLVO();
                queryAICustomSQLVO.setSql(sql);
                chatHistory = ChatHistory.builder()
                        .chatRole(ChatHistoryRoleEnum.MODEL.getValue())
                        .chatId(chatId)
                        .modelId(chat.getModelId())
                        .status(ChatHistoryStatusEnum.FAIL.getValue())
                        .execMessage("数据源异常")
                        .content(JSONUtil.toJsonStr(queryAICustomSQLVO))
                        .build();
                chatHistoryService.save(chatHistory);
            }
            notifyMessageEnd(loginUser.getId(), MessageStatusEnum.ERROR);
            return;
        }
        notifyMessageEnd(loginUser.getId(), MessageStatusEnum.END);
    }

    /**
     * 保存聊天记录
     * @param model 对话人类型
     * @param chatId 对话id
     * @param chat 模型
     * @param content 查询结果
     */
    private ChatHistory saveChatHistory(ChatHistoryRoleEnum model, Long chatId, Chat chat, String content) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setChatRole(model.getValue());
        chatHistory.setChatId(chatId);
        chatHistory.setModelId(chat.getModelId());
        chatHistory.setContent(content);
        chatHistoryService.save(chatHistory);
        return chatHistory;
    }

    /**
     * 根据sql获取对应的查询结果
     * @param loginUser 用户
     * @param datasourceId 数据源id
     * @param sql 查询sql
     * @param chatId 对话id
     * @param chat 聊天记录
     * @return 查询结果
     */
    @Nullable
    private QueryAICustomSQLVO getQueryAICustomSQLVO(User loginUser, Long datasourceId, String sql, Long chatId, Chat chat) throws SQLException {
        return buildUserChatForSqlVO(datasourceId, sql);
    }

    /**
     * 查询对应数据源所有元数据（表信息、表字段）
     * @param loginUser 用户
     * @param datasourceId 数据源id
     * @return 数据源元信息
     */
    public List<AskAIWithDataTablesAndFieldsRequest> getAskAIWithDataTablesAndFieldsRequests(User loginUser, Long datasourceId) {
        // 获取对应数据源所有表信息
        List<CoreDatasetTable> tables = coreDatasourceService.getTablesByDatasourceId(datasourceId, loginUser);
        ThrowUtils.throwIf(tables.isEmpty(), ErrorCode.PARAMS_ERROR, "数据源暂无数据");
        List<AskAIWithDataTablesAndFieldsRequest> dataTablesAndFieldsRequests = new ArrayList<>();
        tables.forEach(table -> {
            // 查询所有字段
            LambdaQueryWrapper<CoreDatasetTableField> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CoreDatasetTableField::getDatasetTableId, table.getId());
            List<CoreDatasetTableField> tableFields = coreDatasetTableFieldService.list(wrapper);
            AskAIWithDataTablesAndFieldsRequest askAIWithDataTablesAndFieldsRequest = AskAIWithDataTablesAndFieldsRequest.builder()
                    .tableId(table.getId())
                    .tableComment(table.getName())
                    .tableName(table.getTableName())
                    .coreDatasetTableFieldList(tableFields)
                    .build();
            dataTablesAndFieldsRequests.add(askAIWithDataTablesAndFieldsRequest);
        });
        return dataTablesAndFieldsRequests;
    }


    /**
     * 通知用户结束OR异常
     * @param userId 用户ID
     * @param messageStatusEnum 消息状态枚举
     */
    public void notifyMessageEnd(Long userId, MessageStatusEnum messageStatusEnum) {
        AskSQLWebSocketMsgVO end = new AskSQLWebSocketMsgVO();
        end.setType(messageStatusEnum.getStatus());
        askSQLWebSocket.sendOneMessage(userId, end);
    }

    /**
     * 执行SQL并封装智能问数返回类
     * @param datasourceId 数据源id
     * @param sql 执行sql
     * @return 智能问数返回类
     */
    public QueryAICustomSQLVO buildUserChatForSqlVO(Long datasourceId, String sql) throws SQLException {
        return datasourceEngine.execSelectSqlToQueryAICustomSQLVO(datasourceId, sql);
    }

    /**
     * 根据数据源一键生成AI图表
     * @param datasourceId 数据源id
     * @param loginUser 登录用户
     */
    public String genChartByAi(Long datasourceId, User loginUser) {
        List<AskAIWithDataTablesAndFieldsRequest> askAIWithDataTablesAndFieldsRequests = this.getAskAIWithDataTablesAndFieldsRequests(loginUser, datasourceId);
        String input = String.format(
                "数据源元数据：%s\n",
                buildGenChartInput(askAIWithDataTablesAndFieldsRequests)
        );
        return aiManager.doChatWithKimi32K(input, AI_GEN_CHART);
    }

    /**
     * 构造智能问数的问题
     * @param dataTablesAndFieldsRequests 数据源元数据
     * @param question 问题
     * @return
     * 示例：
     * 分析需求：%s,
     * [
     * {表名: %s, 表注释： %s, 字段列表:[{%s}、{%s}]}
     * {表名: %s, 表注释： %s, 字段列表:[{%s}、{%s}]}
     * ]
     */
    public String buildAskAISQLInput(List<AskAIWithDataTablesAndFieldsRequest> dataTablesAndFieldsRequests, String question) {
        StringBuilder res = new StringBuilder();
        // 1. 构造需求
        res.append(String.format(ANALYSIS_QUESTION, question));
        res.append(SPLIT);
        // 2. 构造表与字段信息
        StringBuilder tablesAndFields = new StringBuilder();
        dataTablesAndFieldsRequests.forEach(tableAndFields -> {
            // 构造当前表字段列表
            StringBuilder tableFieldsInfo = new StringBuilder();
            List<CoreDatasetTableField> fieldList = tableAndFields.getCoreDatasetTableFieldList();
            fieldList.forEach(field -> {
                tableFieldsInfo.append(String.format(FIELDS_INFO, field.getOriginName(), field.getName(), field.getType()));
                tableFieldsInfo.append(SPLIT);
            });
            // 构造当前表信息
            String tableFieldsInfoList = String.format(LIST_INFO, tableFieldsInfo);
            tablesAndFields.append(String.format(TABLE_INFO, tableAndFields.getTableName(), tableAndFields.getTableComment(), tableFieldsInfoList));
            tableFieldsInfo.append(SPLIT);
        });
        res.append(String.format(TABLES_AND_FIELDS_PART, tablesAndFields));
        return res.toString();
    }


    /**
     * 构造生成图表输入
     * @param dataTablesAndFieldsRequests 表和字段元信息
     * @return 图表输入
     */
    public String buildGenChartInput(List<AskAIWithDataTablesAndFieldsRequest> dataTablesAndFieldsRequests) {
        StringBuilder res = new StringBuilder();
        // 1. 构造需求
        // 2. 构造表与字段信息
        StringBuilder tablesAndFields = new StringBuilder();
        dataTablesAndFieldsRequests.forEach(tableAndFields -> {
            // 构造当前表字段列表
            StringBuilder tableFieldsInfo = new StringBuilder();
            List<CoreDatasetTableField> fieldList = tableAndFields.getCoreDatasetTableFieldList();
            fieldList.forEach(field -> {
                tableFieldsInfo.append(String.format(FIELDS_INFO, field.getOriginName(), field.getName(), field.getType()));
                tableFieldsInfo.append(SPLIT);
            });
            // 构造当前表信息
            String tableFieldsInfoList = String.format(LIST_INFO, tableFieldsInfo);
            tablesAndFields.append(String.format(TABLE_INFO, tableAndFields.getTableName(), tableAndFields.getTableComment(), tableFieldsInfoList));
            tableFieldsInfo.append(SPLIT);
        });
        res.append(String.format(TABLES_AND_FIELDS_PART, tablesAndFields));
        return res.toString();
    }
}
