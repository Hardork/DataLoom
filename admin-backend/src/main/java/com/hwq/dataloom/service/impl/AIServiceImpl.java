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

import static com.hwq.dataloom.constant.UserChatForSQLConstant.*;

/**
 * @author HWQ
 * @date 2024/9/7 16:54
 * @description
 */
@Component
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
        // 2. 获取数据源所有的元数据
        Long datasourceId = chat.getDatasourceId();
        List<AskAIWithDataTablesAndFieldsRequest> dataTablesAndFieldsRequests = getAskAIWithDataTablesAndFieldsRequests(loginUser, datasourceId);
        // 3. 构造请求AI的输入
        String input = buildAskAISQLInput(dataTablesAndFieldsRequests, question);
        // 4. 持久化消息
        ChatHistory user_q = new ChatHistory();
        user_q.setChatRole(ChatHistoryRoleEnum.USER.getValue());
        user_q.setChatId(chatId);
        user_q.setModelId(chat.getModelId());
        user_q.setContent(question);
        chatHistoryService.save(user_q);
        // 5. 利用webSocket发送消息通知开始
        AskSQLWebSocketMsgVO askSQLWebSocketMsgVO = new AskSQLWebSocketMsgVO();
        askSQLWebSocketMsgVO.setType(MessageStatusEnum.START.getStatus());
        askSQLWebSocket.sendOneMessage(loginUser.getId(), askSQLWebSocketMsgVO);
        // 6. 询问AI，获取返回的SQL
        String sql = aiManager.doAskSQLWithKimi(input, LIMIT_RECORDS);
        // 7. 执行SQL，并得到返回的结果
        QueryAICustomSQLVO queryAICustomSQLVO = getQueryAICustomSQLVO(loginUser, datasourceId, sql, chatId, chat);
        if (queryAICustomSQLVO == null) return;
        // 8. 将查询的结果存放在数据库中
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setChatRole(ChatHistoryRoleEnum.MODEL.getValue());
        chatHistory.setChatId(chatId);
        chatHistory.setModelId(chat.getModelId());
        // 9. 存储结果类JSON字符串
        chatHistory.setContent(JSONUtil.toJsonStr(queryAICustomSQLVO));
        try {
            chatHistoryService.save(chatHistory);
        } catch (Exception e) {
            notifyMessageEnd(loginUser.getId());
            return;
        }
        // 10. 利用webSocket发送消息通知
        AskSQLWebSocketMsgVO res = AskSQLWebSocketMsgVO.builder()
                .res(queryAICustomSQLVO.getRes())
                .columns(queryAICustomSQLVO.getColumns())
                .type(MessageStatusEnum.RUNNING.getStatus())
                .sql(sql)
                .build();
        askSQLWebSocket.sendOneMessage(loginUser.getId(), res);
        // 11. 通知结束
        notifyMessageEnd(loginUser.getId());
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
    private QueryAICustomSQLVO getQueryAICustomSQLVO(User loginUser, Long datasourceId, String sql, Long chatId, Chat chat) {
        QueryAICustomSQLVO queryAICustomSQLVO;
        try {
            queryAICustomSQLVO = buildUserChatForSqlVO(datasourceId, sql);
        } catch (Exception e) { // 防止异常发生，前端还继续等待接收数据
            if (e instanceof SQLException) { // 记录异常
                queryAICustomSQLVO = new QueryAICustomSQLVO();
                queryAICustomSQLVO.setSql(sql);
                ChatHistory chatHistory = ChatHistory.builder()
                        .chatRole(ChatHistoryRoleEnum.MODEL.getValue())
                        .chatId(chatId)
                        .modelId(chat.getModelId())
                        .status(ChatHistoryStatusEnum.FAIL.getValue())
                        .execMessage("数据源异常")
                        .content(JSONUtil.toJsonStr(queryAICustomSQLVO))
                        .build();
                chatHistoryService.updateById(chatHistory);
            }
            notifyMessageEnd(loginUser.getId());
            return null;
        }
        return queryAICustomSQLVO;
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

    public void notifyMessageEnd(Long userId) {
        AskSQLWebSocketMsgVO end = new AskSQLWebSocketMsgVO();
        end.setType(MessageStatusEnum.END.getStatus());
        askSQLWebSocket.sendOneMessage(userId, end);
    }

    /**
     * 执行SQL并封装智能问数返回类
     * @param datasourceId 数据源id
     * @param sql 执行sql
     * @return 智能问数返回类
     */
    private QueryAICustomSQLVO buildUserChatForSqlVO(Long datasourceId, String sql) throws SQLException {
        return datasourceEngine.execSelectSqlToQueryAICustomSQLVO(datasourceId, sql);
    }

    /**
     * 根据数据源一键生成AI图表
     * @param datasourceId
     * @param loginUser
     * @return 图表数据配置数组（JSON）
     * [
     *   {
     *     "chartType": "line",
     *     "chartName": "北京空气质量变化趋势",
     *     "dataTableName": "excel_108_北京空气质量",
     *     "seriesArray": [
     *       {"fieldName": "AQI", "rollup": "SUM"},
     *       {"fieldName": "PM2_5", "rollup": "SUM"},
     *       {"fieldName": "PM10", "rollup": "SUM"},
     *       {"fieldName": "S02", "rollup": "SUM"},
     *       {"fieldName": "NO2", "rollup": "SUM"},
     *       {"fieldName": "CO", "rollup": "SUM"}
     *     ],
     *     "group": [
     *       {"fieldName": "日期"}
     *     ]
     *   }
     * ]
     */
    public String genChartByAi(Long datasourceId, User loginUser) {
        List<AskAIWithDataTablesAndFieldsRequest> askAIWithDataTablesAndFieldsRequests = this.getAskAIWithDataTablesAndFieldsRequests(loginUser, datasourceId);
        String input = buildGenChartInput(askAIWithDataTablesAndFieldsRequests);
        return aiManager.doAiGenChartByDatasource(input, false, loginUser);
    }

    /**
     * 构造智能问数的问题
     * @param dataTablesAndFieldsRequests 数据源元数据
     * @param question
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
