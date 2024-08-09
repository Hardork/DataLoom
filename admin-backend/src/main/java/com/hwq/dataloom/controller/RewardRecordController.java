package com.hwq.dataloom.controller;

import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.entity.User;
import com.hwq.dataloom.service.RewardRecordService;
import com.hwq.dataloom.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author:HWQ
 * @DateTime:2023/9/22 21:42
 * @Description:
 **/
@RestController
@RequestMapping("/reward")
@Slf4j
public class RewardRecordController {
    @Resource
    private UserService userService;

    @Resource
    private RewardRecordService rewardRecordService;
    @GetMapping("/add")
    public BaseResponse<Boolean> addReward(HttpServletRequest request) {

        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        boolean res =  rewardRecordService.addReward(loginUser);
        return ResultUtils.success(res);
    }
}
