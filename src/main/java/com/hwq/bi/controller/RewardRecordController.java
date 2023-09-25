package com.hwq.bi.controller;

import com.hwq.bi.common.BaseResponse;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.common.ResultUtils;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.model.dto.post.PostAddRequest;
import com.hwq.bi.model.entity.Post;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.service.RewardRecordService;
import com.hwq.bi.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
