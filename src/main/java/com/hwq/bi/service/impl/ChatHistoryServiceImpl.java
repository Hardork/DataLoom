package com.hwq.bi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.bi.model.entity.ChatHistory;
import com.hwq.bi.service.ChatHistoryService;
import com.hwq.bi.mapper.ChatHistoryMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author HWQ
* @description 针对表【chat_history】的数据库操作Service实现
* @createDate 2023-10-03 00:51:59
*/
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>
    implements ChatHistoryService{

    @Override
    public List<ChatHistory> getUserChatRecord(Long chatId) {
        QueryWrapper<ChatHistory> qw = new QueryWrapper<>();
        qw.eq("chatId", chatId);
        return this.list(qw);
    }
}




