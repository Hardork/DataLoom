package com.hwq.bi.controller;

import com.hwq.bi.common.BaseResponse;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.common.ResultUtils;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.model.entity.Chart;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.vo.GetCurMonthServiceRecordVO;
import com.hwq.bi.service.ServiceRecordService;
import com.hwq.bi.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author:HWQ
 * @DateTime:2023/9/29 16:03
 * @Description:
 **/
@RestController
@Slf4j
@RequestMapping("/serviceRecord")
public class ServiceRecordController {

    @Resource
    private UserService userService;
    @Resource
    private ServiceRecordService service;

    @ApiOperation("查询当前月份用户Bi服务调用情况")
    @GetMapping("/curMonthRecord/Bi")
    public BaseResponse<GetCurMonthServiceRecordVO> getUserCurMonthBiRecord(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        GetCurMonthServiceRecordVO res = service.getUserCurMonthBiRecord(loginUser);
        return ResultUtils.success(res);
    }
    @ApiOperation("查询当前月份用户Ai服务调用情况")
    @GetMapping("/curMonthRecord/Ai")
    public BaseResponse<GetCurMonthServiceRecordVO> getUserCurMonthAiRecord(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        GetCurMonthServiceRecordVO res = service.getUserCurMonthAiRecord(loginUser);
        return ResultUtils.success(res);
    }
}
