package com.hwq.dataloom.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.model.entity.ChatHistory;
import com.hwq.dataloom.model.enums.ChatHistoryRoleEnum;
import com.hwq.dataloom.model.enums.ChatHistoryStatusEnum;
import com.hwq.dataloom.model.vo.ai.ColumnsVO;
import com.hwq.dataloom.model.vo.ai.GetUserSQLChatRecordVO;
import com.hwq.dataloom.model.vo.data.QueryAICustomSQLVO;
import com.hwq.dataloom.service.ChatHistoryService;
import com.hwq.dataloom.mapper.ChatHistoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author HWQ
* @description 针对表【chat_history】的数据库操作Service实现
* @createDate 2023-10-03 00:51:59
*/
@Service
@Slf4j
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>
    implements ChatHistoryService{

    @Override
    public List<ChatHistory> getUserChatRecord(Long chatId) {
        QueryWrapper<ChatHistory> qw = new QueryWrapper<>();
        qw.eq("chatId", chatId);
        List<ChatHistory> res = null;
        try {
            res = this.list(qw);
        } catch (Exception e) {
            log.error("查询对话历史异常, 错误信息{}",  e.getMessage());
        }
        return res;
    }

    @Override
    public List<GetUserSQLChatRecordVO> getUserSQLChatRecord(Long chatId) {
        QueryWrapper<ChatHistory> qw = new QueryWrapper<>();
        qw.eq("chatId", chatId);
        List<ChatHistory> list = this.list(qw);
        return list.stream().map(item -> {
            String content = item.getContent();
            GetUserSQLChatRecordVO getUserSQLChatRecordVO = new GetUserSQLChatRecordVO();
            if (ChatHistoryStatusEnum.FAIL.getValue().equals(item.getStatus())) { // 失败状态
                getUserSQLChatRecordVO.setStatus(ChatHistoryStatusEnum.FAIL.getValue());
                getUserSQLChatRecordVO.setSql("");
                getUserSQLChatRecordVO.setRes(new ArrayList<>());
                return getUserSQLChatRecordVO;
            }
            // TODO: 根据状态判断是否要转字段
            if (item.getChatRole().equals(ChatHistoryRoleEnum.USER.getValue())) { // 用户
                getUserSQLChatRecordVO.setId(item.getId());
                getUserSQLChatRecordVO.setContent(content);
                getUserSQLChatRecordVO.setChatRole(item.getChatRole());
                getUserSQLChatRecordVO.setModelId(item.getModelId());
                getUserSQLChatRecordVO.setChatId(item.getChatId());
                return getUserSQLChatRecordVO;
            }
            QueryAICustomSQLVO aiCustomSQLVO = JSONUtil.toBean(content, QueryAICustomSQLVO.class);
            List<ColumnsVO> columns = aiCustomSQLVO.getColumns().stream().map(column -> {
                ColumnsVO columnsVO = new ColumnsVO();
                columnsVO.setDataIndex(column);
                columnsVO.setTitle(column);
                return columnsVO;
            }).collect(Collectors.toList());
            getUserSQLChatRecordVO.setId(item.getId());
            getUserSQLChatRecordVO.setModelId(item.getModelId());
            getUserSQLChatRecordVO.setChatId(item.getChatId());
            getUserSQLChatRecordVO.setChatRole(item.getChatRole());
            getUserSQLChatRecordVO.setColumns(columns);
            getUserSQLChatRecordVO.setRes(aiCustomSQLVO.getRes());
            getUserSQLChatRecordVO.setSql(aiCustomSQLVO.getSql());
            return getUserSQLChatRecordVO;
        }).collect(Collectors.toList());
    }
}




