package com.hwq.bi.service.impl;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.entity.UserMessage;
import com.hwq.bi.model.enums.UserMessageIsReadEnum;
import com.hwq.bi.service.UserMessageService;
import com.hwq.bi.mapper.UserMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author HWQ
* @description 针对表【user_message】的数据库操作Service实现
* @createDate 2023-09-24 22:18:07
*/
@Service
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage>
    implements UserMessageService{

    @Override
    public Boolean readAllUnReadMessage(User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        QueryWrapper<UserMessage> userMessageQueryWrapper = new QueryWrapper<>();
        userMessageQueryWrapper.eq("userId", loginUser.getId()).eq("isRead", UserMessageIsReadEnum.UNREAD.getValue());
        List<UserMessage> list = this.list();
        // 有才更新
        if (list.size() > 0) {
            UpdateWrapper<UserMessage> userMessageUpdateWrapper = new UpdateWrapper<>();
            userMessageUpdateWrapper.eq("userId", loginUser.getId()).eq("isRead", UserMessageIsReadEnum.UNREAD.getValue());
            userMessageUpdateWrapper.setSql("isRead = " + UserMessageIsReadEnum.IS_READ.getValue());
            boolean update = this.update(userMessageUpdateWrapper);
            ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR);
        }
        return true;
    }

    @Override
    public void validMessage(UserMessage userMessage, boolean b) {
     Long userId = userMessage.getUserId();
     ThrowUtils.throwIf(userId == null || userId < 0, ErrorCode.PARAMS_ERROR);
    }
}




