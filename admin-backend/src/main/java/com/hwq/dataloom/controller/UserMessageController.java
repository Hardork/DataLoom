package com.hwq.dataloom.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.dto.user_massage.UserMessageAddRequest;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.entity.UserMessage;
import com.hwq.dataloom.model.enums.UserMessageIsReadEnum;
import com.hwq.dataloom.service.UserMessageService;
import com.hwq.dataloom.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author:HWQ
 * @DateTime:2023/9/25 10:44
 * @Description:
 **/
@RestController
@RequestMapping("/message")
@Slf4j
public class UserMessageController {

    @Resource
    private UserMessageService userMessageService;

    @Resource
    private UserService userService;
    /**
     * 将用户所有的未读消息设置未已读消息
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addUserMessage(@RequestBody UserMessageAddRequest userMessageAddRequest, HttpServletRequest request) {
        if (userMessageAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserMessage userMessage = new UserMessage();
        BeanUtils.copyProperties(userMessageAddRequest, userMessage);
        userMessageService.validMessage(userMessage, true);
        boolean result = userMessageService.save(userMessage);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long messageId = userMessage.getId();
        return ResultUtils.success(messageId);
    }

    @PostMapping("/read")
    public BaseResponse<Boolean> hasReadMessage(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        Boolean res = userMessageService.readAllUnReadMessage(loginUser);
        return ResultUtils.success(res);
    }

    @PostMapping("/list")
    public BaseResponse<List<UserMessage>> listUnReadMessage(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        QueryWrapper<UserMessage> userMessageQueryWrapper = new QueryWrapper<>();
        userMessageQueryWrapper.eq("userId", loginUser.getId()).eq("isRead", UserMessageIsReadEnum.UNREAD.getValue()).orderByDesc("createTime");
        List<UserMessage> list = userMessageService.list(userMessageQueryWrapper);
        return ResultUtils.success(list);
    }
}
