package com.hwq.bi.controller;

import com.hwq.bi.annotation.CheckPoint;
import com.hwq.bi.annotation.ReduceRewardPoint;
import com.hwq.bi.common.BaseResponse;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.common.ResultUtils;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.manager.SparkAiManager;
import com.hwq.bi.model.dto.ai.AiTalkRequest;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author:HWQ
 * @DateTime:2023/9/25 20:42
 * @Description:
 **/
@RestController
@Slf4j
@RequestMapping("/Ai")
public class AiController {
    @Resource
    private UserService userService;
    @Resource
    private SparkAiManager sparkAiManager;

    @ReduceRewardPoint(reducePoint = 1)
    @CheckPoint(needPoint = 1)
    @PostMapping("/talk")
    public BaseResponse<Boolean> getAiTalk(@RequestBody AiTalkRequest aiTalkRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(aiTalkRequest == null || StringUtils.isEmpty(aiTalkRequest.getText()), ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        sparkAiManager.setUserId(loginUser.getId());
        sparkAiManager.startTalk(aiTalkRequest.getText());
        return ResultUtils.success(true);
    }
}
