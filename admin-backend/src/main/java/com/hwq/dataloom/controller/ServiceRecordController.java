package com.hwq.dataloom.controller;

import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.vo.GetCurMonthServiceRecordVO;
import com.hwq.dataloom.service.ServiceRecordService;
import com.hwq.dataloom.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/admin/serviceRecord")
public class ServiceRecordController {

    @Resource
    private UserService userService;
    @Resource
    private ServiceRecordService service;

    @Operation(summary = "查询当前月份用户Bi服务调用情况")
    @GetMapping("/curMonthRecord/Bi")
    public BaseResponse<GetCurMonthServiceRecordVO> getUserCurMonthBiRecord(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        GetCurMonthServiceRecordVO res = service.getUserCurMonthBiRecord(loginUser);
        return ResultUtils.success(res);
    }
    @Operation(summary = "查询当前月份用户Ai服务调用情况")
    @GetMapping("/curMonthRecord/Ai")
    public BaseResponse<GetCurMonthServiceRecordVO> getUserCurMonthAiRecord(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        GetCurMonthServiceRecordVO res = service.getUserCurMonthAiRecord(loginUser);
        return ResultUtils.success(res);
    }
}
