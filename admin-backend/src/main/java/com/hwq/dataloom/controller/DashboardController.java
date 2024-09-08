package com.hwq.dataloom.controller;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.dashboard.AddDashboardRequestDTO;
import com.hwq.dataloom.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @author HWQ
 * @date 2024/9/8 12:26
 * @description 仪表盘接口
 */
@RestController
@RequestMapping("/admin/dashboard")
@Tag(name = "仪表盘接口")
public class DashboardController {

    @Resource
    private UserService userService;

    @Operation(summary = "添加仪表盘")
    @PostMapping("/add")
    public BaseResponse<Boolean> addDashboard(@RequestBody @Valid AddDashboardRequestDTO addDashboardRequestDTO, HttpServletRequest request) {
        // TODO:
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        return ResultUtils.success();
    }

    @Operation(summary = "新建图表")
    @PostMapping("/addChart")
    public BaseResponse<Boolean> addChart(@RequestBody @Valid AddDashboardRequestDTO addDashboardRequestDTO, HttpServletRequest request) {
        // TODO:
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        return ResultUtils.success();
    }

    @Operation(summary = "获取仪表盘所有的图表")
    @GetMapping("/list")
    public BaseResponse<Boolean> listAllChart(@RequestBody @Valid AddDashboardRequestDTO addDashboardRequestDTO, HttpServletRequest request) {
        // TODO:
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        return ResultUtils.success();
    }
}
