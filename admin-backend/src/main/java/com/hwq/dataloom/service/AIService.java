package com.hwq.dataloom.service;

import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.ai.AskAIWithDataTablesAndFieldsRequest;
import com.hwq.dataloom.model.dto.ai.ChatForSQLRequest;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/9/7 16:53
 * @description AI Service层
 */
public interface AIService {
    /**
     * 智能问数
     * @param chatForSQLRequest 智能问数请求类
     * @param loginUser 用户
     */
    void userChatForSQL(ChatForSQLRequest chatForSQLRequest, User loginUser);

    /**
     * 获取数据源所有表与字段信息
     * @param loginUser 用户信息
     * @param datasourceId 数据源id
     * @return 数据源所有表与字段信息
     */
    List<AskAIWithDataTablesAndFieldsRequest> getAskAIWithDataTablesAndFieldsRequests(User loginUser, Long datasourceId);


    /**
     * 构造AI询问SQL输入
     * @param dataTablesAndFieldsRequests 数据源所有表与字段信息
     * @param question 问题
     * @return AI询问对应SQL
     */
    String buildAskAISQLInput(List<AskAIWithDataTablesAndFieldsRequest> dataTablesAndFieldsRequests, String question);

}
