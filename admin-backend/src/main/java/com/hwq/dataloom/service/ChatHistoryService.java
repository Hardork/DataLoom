package com.hwq.dataloom.service;

import com.hwq.dataloom.model.entity.ChatHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.model.vo.ai.GetUserSQLChatRecordVO;

import java.util.List;

/**
* @author HWQ
* @description 针对表【chat_history】的数据库操作Service
* @createDate 2023-10-03 00:51:59
*/
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 获取用户历史聊天记录
     * @param chatId
     * @return
     */
    List<ChatHistory> getUserChatRecord(Long chatId);

    /**
     * 获取智能问数历史聊天记录
     * @param chatId
     * @return
     */
    List<GetUserSQLChatRecordVO> getUserSQLChatRecord(Long chatId);
}
