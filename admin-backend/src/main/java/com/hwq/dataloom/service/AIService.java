package com.hwq.dataloom.service;

import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.ai.ChatForSQLRequest;

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
}
