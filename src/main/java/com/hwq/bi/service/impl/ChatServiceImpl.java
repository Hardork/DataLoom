package com.hwq.bi.service.impl;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.entity.AiRole;
import com.hwq.bi.model.entity.Chat;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.entity.UserCreateAssistant;
import com.hwq.bi.model.vo.GetUserChatHistoryVO;
import com.hwq.bi.service.AiRoleService;
import com.hwq.bi.service.ChatService;
import com.hwq.bi.mapper.ChatMapper;
import com.hwq.bi.service.UserCreateAssistantService;
import com.hwq.bi.service.UserMessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author HWQ
* @description 针对表【chat】的数据库操作Service实现
* @createDate 2023-10-03 00:51:55
*/
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat>
    implements ChatService{

    @Resource
    private AiRoleService aiRoleService;
    @Resource
    private UserCreateAssistantService userCreateAssistantService;

    @Override
    public List<GetUserChatHistoryVO> getUserChatHistory(User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        QueryWrapper<Chat> qw = new QueryWrapper<>();
        qw.eq("userId", loginUser.getId());
        List<Chat> list = this.list(qw);
        List<GetUserChatHistoryVO> res = new ArrayList<>();
        for (Chat chat : list) {
            Long id = chat.getId();
            Long modelId = chat.getModelId();
            // modelId查询对应的助手信息
            AiRole aiRole = aiRoleService.getById(modelId);
            if(aiRole == null) { //可能是用户创建的助手
                UserCreateAssistant userCreateAssistant = userCreateAssistantService.getById(modelId);
                GetUserChatHistoryVO getUserChatHistoryVO = new GetUserChatHistoryVO();
                getUserChatHistoryVO.setChatId(id);
                getUserChatHistoryVO.setAssistantName(userCreateAssistant.getAssistantName());
                getUserChatHistoryVO.setFunctionDes(userCreateAssistant.getFunctionDes());
                res.add(getUserChatHistoryVO);
            }
            // 填充信息
            if (aiRole != null) {
                GetUserChatHistoryVO getUserChatHistoryVO = new GetUserChatHistoryVO();
                getUserChatHistoryVO.setChatId(id);
                getUserChatHistoryVO.setAssistantName(aiRole.getAssistantName());
                getUserChatHistoryVO.setFunctionDes(aiRole.getFunctionDes());
                res.add(getUserChatHistoryVO);
            }
        }
        return res;
    }

    @Override
    public Boolean addUserChatHistory(Long modelId, User loginUser) {
        ThrowUtils.throwIf(loginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        ThrowUtils.throwIf(modelId == null, ErrorCode.PARAMS_ERROR);
        // 添加记录
        Chat chat = new Chat();
        chat.setUserId(loginUser.getId());
        chat.setModelId(modelId);
        boolean save = this.save(chat);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        return true;
    }
}




