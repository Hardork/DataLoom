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
import com.hwq.dataloom.model.json.ai.UserChatForSQLRes;
import com.hwq.dataloom.model.vo.data.QueryAICustomSQLVO;
import com.hwq.dataloom.service.*;
import com.hwq.dataloom.service.basic.strategy.DatasourceExecuteStrategy;
import com.hwq.dataloom.service.basic.strategy.DatasourceStrategyChoose;
import com.hwq.dataloom.utils.datasource.CustomPage;
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

import static com.hwq.dataloom.constant.PromptConstants.*;
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
    private ChatService chatService;

    @Resource
    private AiManager aiManager;


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
        // 获取模型ID
        Chat chat = chatService.getById(chatId);
        ThrowUtils.throwIf(chat == null, ErrorCode.PARAMS_ERROR, "不存在该助手");
        // 持久化用户消息
        ChatHistory chatHistory = saveChatHistory(ChatHistoryRoleEnum.USER, chatId, chat, question, false);
        // 发送消息通知开始
        notify(ChatHistoryStatusEnum.START, loginUser.getId(), "开始对话");
        // 分析数据源
        Long datasourceId = chat.getDatasourceId();
        List<AskAIWithDataTablesAndFieldsRequest> dataTablesAndFieldsRequests;
        try {
            dataTablesAndFieldsRequests = getAskAIWithDataTablesAndFieldsRequests(loginUser, datasourceId);
        } catch (SQLException e) {
            log.error("智能问数 消息ID: {} 数据源ID:{} 获取数据源表和字段信息失败\n 失败原因:{}",  chatHistory.getId(), datasourceId, e.getMessage());
            notifyMessageEnd(loginUser.getId(), ChatHistoryStatusEnum.ERROR, "数据源异常");
            return;
        }
        // 发送分析数据源完毕
        notifyAndUpdateStatus(chatHistory, ChatHistoryStatusEnum.ANALYSIS_COMPLETE, loginUser.getId(), "分析数据源完毕");
        // 结合Prompt构造查询 (关联表、统计查询记录数sql、查询sql)
        String input = buildAskAISQLInput(dataTablesAndFieldsRequests, question);
        log.info("智能问数 消息ID: {}  AI输入: {}", chatHistory.getId(), input);
        String answer = aiManager.doChatWithKimi32K(input, NEW_PROMPT);
        UserChatForSQLRes userChatForSQLRes;
        // 序列化结果
        try {
            userChatForSQLRes = JSONUtil.toBean(answer, UserChatForSQLRes.class, false);
        } catch (Exception e) {
            log.error("智能问数 消息ID: {} 序列化失败 返回answer: {}", chatHistory.getId(), answer);
            notifyMessageEnd(loginUser.getId(), ChatHistoryStatusEnum.ERROR, "数据源异常");
            return;
        }
        // 发送分析关联表完毕
        notify(ChatHistoryStatusEnum.ANALYSIS_RELATE_TABLE_COMPLETE, loginUser.getId(), JSONUtil.toJsonStr(userChatForSQLRes));
        try {
            // 执行SQL，并得到返回的结果
            CustomPage<Map<String, Object>> dataPage = getQueryAICustomSQLVO(datasourceId, userChatForSQLRes);
            log.info("消息ID:{}, 智能问数查询结果: {}", chatHistory.getId(), dataPage);
            // 将查询的结果存放在数据库中
            saveChatHistory(ChatHistoryRoleEnum.MODEL, chatId, chat, JSONUtil.toJsonStr(dataPage), dataPage.getTotal() > 10);
            // 发送消息通知结果
            AskSQLWebSocketMsgVO res = AskSQLWebSocketMsgVO.builder()
                    .data(dataPage)
                    .type(MessageStatusEnum.END.getValue())
                    .build();
            askSQLWebSocket.sendOneMessage(loginUser.getId(), res);
        } catch (Exception e) { // 异常处理
            if (e instanceof SQLException) {
                QueryAICustomSQLVO queryAICustomSQLVO = new QueryAICustomSQLVO();
                queryAICustomSQLVO.setSql(queryAICustomSQLVO.getSql());
                chatHistory = ChatHistory.builder()
                        .chatRole(ChatHistoryRoleEnum.MODEL.getValue())
                        .chatId(chatId)
                        .modelId(chat.getModelId())
                        .status(ChatHistoryStatusEnum.ERROR.getValue())
                        .execMessage("数据源异常")
                        .content(JSONUtil.toJsonStr(queryAICustomSQLVO))
                        .build();
                chatHistoryService.save(chatHistory);
            }
            notifyMessageEnd(loginUser.getId(), ChatHistoryStatusEnum.ERROR, "查询数据源异常");
            return;
        }
        notifyMessageEnd(loginUser.getId(), ChatHistoryStatusEnum.END, "对话结束");
    }

    /**
     * 通知消息并更新状态
     * @param chatHistory 对话历史
     * @param statusEnum 智能问数当前状态
     * @param userId 用户ID
     * @param message 通知消息
     */
    private void notifyAndUpdateStatus(ChatHistory chatHistory, ChatHistoryStatusEnum statusEnum, Long userId, String message) {
        chatHistory.setStatus(statusEnum.getValue());
        chatHistoryService.updateById(chatHistory);
        notify(statusEnum, userId, message);
    }

    /**
     * 通知前端智能问数开始
     */
    private void notify(ChatHistoryStatusEnum statusEnum, Long loginUser, String message) {
        AskSQLWebSocketMsgVO askSQLWebSocketMsgVO = new AskSQLWebSocketMsgVO();
        askSQLWebSocketMsgVO.setType(statusEnum.getValue());
        askSQLWebSocketMsgVO.setMessage(message);
        askSQLWebSocket.sendOneMessage(loginUser, askSQLWebSocketMsgVO);
    }

    /**
     * 保存聊天记录
     * @param model 对话人类型
     * @param chatId 对话id
     * @param chat 模型
     * @param content 查询结果
     */
    private ChatHistory saveChatHistory(ChatHistoryRoleEnum model, Long chatId, Chat chat, String content, boolean isOverSize) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setChatRole(model.getValue());
        chatHistory.setChatId(chatId);
        chatHistory.setModelId(chat.getModelId());
        chatHistory.setContent(content);
        chatHistory.setIsOverSize(isOverSize);
        chatHistoryService.save(chatHistory);
        return chatHistory;
    }

    /**
     * 根据sql获取对应的查询结果
     * @param datasourceId 数据源id
     * @param userChatForSQLRes AI返回的结果内容
     * @return 查询结果
     */
    private CustomPage<Map<String, Object>> getQueryAICustomSQLVO(Long datasourceId, UserChatForSQLRes userChatForSQLRes) throws SQLException {
        return buildUserChatForSqlVO(datasourceId, userChatForSQLRes);
    }

    /**
     * 查询对应数据源所有元数据（表信息、表字段）
     * @param loginUser 用户
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
     * @param userId 用户ID
     * @param messageStatusEnum 消息状态枚举
     */
    public void notifyMessageEnd(Long userId, ChatHistoryStatusEnum messageStatusEnum, String message) {
        notify(messageStatusEnum, userId, message);
    }

    /**
     * 执行SQL并封装智能问数返回类
     * @param datasourceId 数据源id
     * @param userChatForSQLRes AI返回的结果
     * @return 智能问数返回类
     */
    public CustomPage<Map<String, Object>> buildUserChatForSqlVO(Long datasourceId, UserChatForSQLRes userChatForSQLRes) throws SQLException {
        // 判断数据源的归属，决定从哪获取数据
        CoreDatasource coreDatasource = coreDatasourceService.getById(datasourceId);
        DatasourceExecuteStrategy executeStrategy = datasourceStrategyChoose.choose(coreDatasource.getType());
        return executeStrategy.getDataFromDatasourceBySql(coreDatasource, userChatForSQLRes);
    }

    /**
     * 根据数据源一键生成AI图表
     * @param datasourceId 数据源id
     * @param loginUser 登录用户
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
