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
import com.hwq.dataloom.model.vo.data.QueryAICustomSQLVO;
import com.hwq.dataloom.service.*;
import com.hwq.dataloom.utils.datasource.DatasourceEngine;
import com.hwq.dataloom.websocket.AskSQLWebSocket;
import com.hwq.dataloom.websocket.vo.AskSQLWebSocketMsgVO;
import lombok.SneakyThrows;
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
        // 2. 获取数据源所有的表信息
        Long datasourceId = chat.getDatasourceId();
        List<CoreDatasetTable> tables = coreDatasourceService.getTablesByDatasourceId(datasourceId, loginUser);
        ThrowUtils.throwIf(tables.isEmpty(), ErrorCode.PARAMS_ERROR, "数据源暂无数据");

        // 3. 查询对应数据源所有元数据（表信息、表字段）
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
        // 4. 构造请求AI的输入
        String input = buildAskAISQLInput(dataTablesAndFieldsRequests, question);
        // 5. 将数据存储在智能问数历史中
        ChatHistory user_q = new ChatHistory();
        user_q.setChatRole(ChatHistoryRoleEnum.USER.getValue());
        user_q.setChatId(chatId);
        user_q.setModelId(chat.getModelId());
        user_q.setContent(question);
        chatHistoryService.save(user_q);
        // 6. 利用webSocket发送消息通知开始
        AskSQLWebSocketMsgVO askSQLWebSocketMsgVO = new AskSQLWebSocketMsgVO();
        askSQLWebSocketMsgVO.setType("start");
        askSQLWebSocket.sendOneMessage(loginUser.getId(), askSQLWebSocketMsgVO);
        // 7. 获取返回的SQL
        String sql = aiManager.doAskSQLWithKimi(input, LIMIT_RECORDS);
        // 8. 执行SQL，并得到返回的结果
        QueryAICustomSQLVO queryAICustomSQLVO = null;
        try {
            queryAICustomSQLVO = buildUserChatForSqlVO(datasourceId, sql);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "查询数据异常");
        }
        // 9. 将查询的结果存放在数据库中
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setChatRole(ChatHistoryRoleEnum.MODEL.getValue());
        chatHistory.setChatId(chatId);
        chatHistory.setModelId(chat.getModelId());
        // 10. 存储结果类JSON字符串
        chatHistory.setContent(JSONUtil.toJsonStr(queryAICustomSQLVO));
        chatHistoryService.save(chatHistory);
        // 11. 利用webSocket发送消息通知
        AskSQLWebSocketMsgVO res = AskSQLWebSocketMsgVO.builder()
                .res(queryAICustomSQLVO.getRes())
                .columns(queryAICustomSQLVO.getColumns())
                .type("running")
                .sql(sql)
                .build();
        askSQLWebSocket.sendOneMessage(loginUser.getId(), res);
        // 12. 通知结束
        AskSQLWebSocketMsgVO end = new AskSQLWebSocketMsgVO();
        end.setType("end");
        askSQLWebSocket.sendOneMessage(loginUser.getId(), end);
    }

    /**
     * 执行SQL并封装智能问数返回类
     * @param datasourceId 数据源id
     * @param sql 执行sql
     * @return 智能问数返回类
     */
    private QueryAICustomSQLVO buildUserChatForSqlVO(Long datasourceId, String sql) {
        return datasourceEngine.execSelectSqlToQueryAICustomSQLVO(datasourceId, sql);
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
    private String buildAskAISQLInput(List<AskAIWithDataTablesAndFieldsRequest> dataTablesAndFieldsRequests, String question) {
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
}
