package com.hwq.dataloom.controller;

import com.hwq.dataloom.config.UserContext;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.service.RewardRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author:HWQ
 * @DateTime:2023/9/22 21:42
 * @Description: 积分奖励
 **/
@RestController
@RequestMapping("/points-service/reward")
@Slf4j
public class RewardRecordController {

    @Resource
    private RewardRecordService rewardRecordService;
    @GetMapping("/add")
    public BaseResponse<Boolean> addReward(HttpServletRequest request) {

        User loginUser = UserContext.getUser();
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        boolean res =  rewardRecordService.addReward(loginUser);
        return ResultUtils.success(res);
    }
}
