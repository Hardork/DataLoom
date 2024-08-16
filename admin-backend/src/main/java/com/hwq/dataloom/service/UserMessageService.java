package com.hwq.dataloom.service;

import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.entity.UserMessage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HWQ
* @description 针对表【user_message】的数据库操作Service
* @createDate 2023-09-24 22:18:07
*/
public interface UserMessageService extends IService<UserMessage> {


    Boolean readAllUnReadMessage(User loginUser);

    void validMessage(UserMessage userMessage, boolean b);
}
