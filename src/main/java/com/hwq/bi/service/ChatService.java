package com.hwq.bi.service;

import com.hwq.bi.model.entity.Chat;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.vo.GetUserChatHistoryVO;

import java.util.List;

/**
* @author HWQ
* @description 针对表【chat】的数据库操作Service
* @createDate 2023-10-03 00:51:55
*/
public interface ChatService extends IService<Chat> {

    /**
     * 获取用户AI历史对话
     * @param loginUser
     * @return
     */
    List<GetUserChatHistoryVO> getUserChatHistory(User loginUser);

    /**
     * 添加用户对话
     * @param modelId
     * @param loginUser
     * @return
     */
    Boolean addUserChatHistory(Long modelId, User loginUser);
}
