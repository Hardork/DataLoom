package com.hwq.dataloom.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.manager.AiManager;
import com.hwq.dataloom.model.dto.ai.AskAIWithDataTablesAndFieldsRequest;
import com.hwq.dataloom.model.dto.ai.ChatForSQLPageRequest;
import com.hwq.dataloom.model.dto.ai.ChatForSQLRequest;
import com.hwq.dataloom.model.entity.*;
import com.hwq.dataloom.model.enums.ChatHistoryRoleEnum;
import com.hwq.dataloom.model.enums.ChatHistoryStatusEnum;
import com.hwq.dataloom.model.vo.data.QueryAICustomSQLVO;
import com.hwq.dataloom.model.vo.data.SaveAICustomSQLVO;
import com.hwq.dataloom.service.*;
import com.hwq.dataloom.service.basic.strategy.DatasourceExecuteStrategy;
import com.hwq.dataloom.service.basic.strategy.DatasourceStrategyChoose;
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

    @Resource
    private DatasourceStrategyChoose datasourceStrategyChoose;


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
        List<AskAIWithDataTablesAndFieldsRequest> dataTablesAndFieldsRequests;
        try {
            dataTablesAndFieldsRequests = getAskAIWithDataTablesAndFieldsRequests(loginUser, datasourceId);
        } catch (SQLException e) {
            log.error("获取数据源表和字段信息失败");
            log.error(e.getMessage());
            notifyMessageEnd(loginUser.getId(), MessageStatusEnum.ERROR);
            return;
        }
        // 4. 构造请求AI的输入
        String input = buildAskAISQLInput(dataTablesAndFieldsRequests, question);
        log.info("智能问数 消息ID: {}  AI输入: {}", chatHistory.getId(), input);
        // 5. 利用webSocket发送消息通知开始
        notify(MessageStatusEnum.START, loginUser.getId());
        // 6. 询问AI，获取返回的SQL
        // TODO: 修改prompt，获取2条SQL，一条查询数据总数，一条查询数据（ps： 使用'\n'分割）
        String prompt = String.format(SQL_ANALYSIS_PROMPT, 200);
        // TODO：发送提取关联表完毕（显示出关联的表（点击可跳转））
        String sql = aiManager.doChatWithKimi32K(input, prompt);
        try {
            // TODO: 根据查询总数判断是否需要进行分页查询
            // 7. 执行SQL，并得到返回的结果
            QueryAICustomSQLVO queryAICustomSQLVO = getQueryAICustomSQLVO(datasourceId, sql);
            log.info("消息ID:{}, 智能问数查询结果: {}", chatHistory.getId(), queryAICustomSQLVO);

            SaveAICustomSQLVO saveAICustomSQLVO = SaveAICustomSQLVO.builder()
                    .columns(queryAICustomSQLVO.getColumns())
                    .res(queryAICustomSQLVO.getRes())
                    .sql(queryAICustomSQLVO.getSql())
//                    .total(queryAICustomSQLVO.)
                    .build();
            saveChatHistory(ChatHistoryRoleEnum.MODEL, chatId, chat, JSONUtil.toJsonStr(saveAICustomSQLVO));
            // 9. 利用webSocket发送消息通知
            AskSQLWebSocketMsgVO res = AskSQLWebSocketMsgVO.builder()
                    .res(queryAICustomSQLVO.getRes())
                    .columns(queryAICustomSQLVO.getColumns())
                    .type(MessageStatusEnum.RUNNING.getStatus())
                    .sql(sql)
//                    .total()
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
     * 通知前端智能问数开始
     */
    private void notify(MessageStatusEnum start, Long loginUser) {
        AskSQLWebSocketMsgVO askSQLWebSocketMsgVO = new AskSQLWebSocketMsgVO();
        askSQLWebSocketMsgVO.setType(start.getStatus());
        askSQLWebSocket.sendOneMessage(loginUser, askSQLWebSocketMsgVO);
    }

    /**
     * 保存聊天记录
     *
     * @param model   对话人类型
     * @param chatId  对话id
     * @param chat    模型
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
     *
     * @param datasourceId 数据源id
     * @param sql          查询sql
     * @return 查询结果
     */
    private QueryAICustomSQLVO getQueryAICustomSQLVO(Long datasourceId, String sql) throws SQLException {
        return buildUserChatForSqlVO(datasourceId, sql);
    }

    /**
     * 查询对应数据源所有元数据（表信息、表字段）
     *
     * @param loginUser    用户
     * @param datasourceId 数据源id
     * @return 数据源元信息
     */
    public List<AskAIWithDataTablesAndFieldsRequest> getAskAIWithDataTablesAndFieldsRequests(User loginUser, Long datasourceId) throws SQLException {
        // 判断数据源的归属，决定从哪获取数据
        CoreDatasource coreDatasource = coreDatasourceService.getById(datasourceId);
        DatasourceExecuteStrategy executeStrategy = datasourceStrategyChoose.choose(coreDatasource.getType());
        return executeStrategy.getAskAIWithDataTablesAndFieldsRequests(coreDatasource, loginUser);
    }


    /**
     * 通知用户结束OR异常
     *
     * @param userId            用户ID
     * @param messageStatusEnum 消息状态枚举
     */
    public void notifyMessageEnd(Long userId, MessageStatusEnum messageStatusEnum) {
        notify(messageStatusEnum, userId);
    }

    /**
     * 执行SQL并封装智能问数返回类
     *
     * @param datasourceId 数据源id
     * @param sql          执行sql
     * @return 智能问数返回类
     */
    public QueryAICustomSQLVO buildUserChatForSqlVO(Long datasourceId, String sql) throws SQLException {
        // 判断数据源的归属，决定从哪获取数据
        CoreDatasource coreDatasource = coreDatasourceService.getById(datasourceId);
        DatasourceExecuteStrategy executeStrategy = datasourceStrategyChoose.choose(coreDatasource.getType());
        return executeStrategy.getDataFromDatasourceBySql(coreDatasource, sql);
    }

    /**
     * 根据数据源一键生成AI图表
     *
     * @param datasourceId 数据源id
     * @param loginUser    登录用户
     */
    public String genChartByAi(Long datasourceId, User loginUser) throws SQLException {
        List<AskAIWithDataTablesAndFieldsRequest> askAIWithDataTablesAndFieldsRequests = this.getAskAIWithDataTablesAndFieldsRequests(loginUser, datasourceId);
        String input = String.format(
                "数据源元数据：%s\n",
                buildGenChartInput(askAIWithDataTablesAndFieldsRequests)
        );
        return aiManager.doChatWithKimi32K(input, AI_GEN_CHART);
    }

    /**
     * 构造智能问数的问题
     *
     * @param dataTablesAndFieldsRequests 数据源元数据
     * @param question                    问题
     * @return 示例：
     * 分析需求：%s,
     * [
     * {表名: %s, 表注释： %s, 字段列表:[{%s}、{%s}]}
     * {表名: %s, 表注释： %s, 字段列表:[{%s}、{%s}]}
     * ]
     */
    public String buildAskAISQLInput(List<AskAIWithDataTablesAndFieldsRequest> dataTablesAndFieldsRequests, String question) {
        // TODO: 将信息缓存
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

    @Override
    public void queryUserChatForSQL(ChatForSQLPageRequest chatForSQLPageRequest, User loginUser) {
        Long chatId = chatForSQLPageRequest.getChatId();
        Chat chat = chatService.getById(chatId);
        Long datasourceId = chat.getDatasourceId();
        String sql = chatForSQLPageRequest.getSql();
        int index = sql.indexOf("LIMIT");
        if (index == -1) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        Integer page = chatForSQLPageRequest.getPage();
        Integer size = chatForSQLPageRequest.getSize();
        String pageStringFormat = " LIMIT %s,%s";
        // 查询语句 + LIMIT分页
        String newSql = sql.substring(0, index) + String.format(pageStringFormat, (page - 1) * size, size);
        try {
            QueryAICustomSQLVO queryAICustomSQLVO = datasourceEngine.execSelectSqlToQueryAICustomSQLVO(datasourceId, newSql);
            AskSQLWebSocketMsgVO res = AskSQLWebSocketMsgVO.builder()
                    .res(queryAICustomSQLVO.getRes())
                    .columns(queryAICustomSQLVO.getColumns())
                    .type(MessageStatusEnum.RUNNING.getStatus())
                    .sql(sql)
                    .build();
            askSQLWebSocket.sendOneMessage(loginUser.getId(), res);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 构造生成图表输入
     *
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
