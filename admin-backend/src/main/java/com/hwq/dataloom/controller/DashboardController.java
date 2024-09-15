package com.hwq.dataloom.controller;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.dashboard.*;
import com.hwq.dataloom.model.entity.ChartOption;
import com.hwq.dataloom.model.entity.Dashboard;
import com.hwq.dataloom.model.vo.dashboard.GetChartDataVO;
import com.hwq.dataloom.service.DashboardService;
import com.hwq.dataloom.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

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

    @Resource
    private DashboardService dashboardService;


    @Operation(summary = "添加仪表盘")
    @PostMapping("/add")
    public BaseResponse<Boolean> addDashboard(@RequestBody @Valid AddDashboardRequestDTO addDashboardRequestDTO, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        dashboardService.addDashboard(addDashboardRequestDTO, loginUser);
        return ResultUtils.success();
    }

    @Operation(summary = "展示用户所有仪表盘")
    @GetMapping("/listAllDashboard")
    public BaseResponse<List<Dashboard>> listAllDashboard(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(dashboardService.listAllDashboard(loginUser));
    }

    @Operation(summary = "根据id查询仪表盘")
    @GetMapping("/getDashboardById")
    public BaseResponse<Dashboard> getDashboardById(Long dashboardId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(dashboardService.getDashboardById(dashboardId, loginUser));
    }

    @PostMapping("/deleteDashboard")
    public BaseResponse<Boolean> deleteDashboard(Long dashboardId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        dashboardService.deleteDashboard(dashboardId, loginUser);
        return ResultUtils.success();
    }


    @Operation(summary = "保存仪表盘配置")
    @PostMapping("/save")
    public BaseResponse<Boolean> saveDashboard(@RequestBody @Valid SaveDashboardRequestDTO saveDashboardRequestDTO, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        dashboardService.saveDashboard(saveDashboardRequestDTO, loginUser);
        return ResultUtils.success();
    }

    @Operation(summary = "添加图表")
    @PostMapping("/addChart")
    public BaseResponse<Long> addDashboardChart(@RequestBody @Valid AddDashboardChartRequestDTO addDashboardChartRequestDTO, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(dashboardService.addChart(addDashboardChartRequestDTO, loginUser));
    }

    @Operation(summary = "编辑图表")
    @PostMapping("/editChart")
    public BaseResponse<Boolean> editChart(@RequestBody @Valid EditDashboardChartRequestDTO editDashboardChartRequestDTO, HttpServletRequest request) {
        // TODO:
        User loginUser = userService.getLoginUser(request);
        dashboardService.editChart(editDashboardChartRequestDTO, loginUser);
        return ResultUtils.success();
    }

    @Operation(summary = "获取仪表盘所有的图表")
    @GetMapping("/listAllChart")
    public BaseResponse<List<ChartOption>> listAllChart(Long dashboardId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<ChartOption> chartOptions = dashboardService.listAllChart(dashboardId, loginUser);
        return ResultUtils.success(chartOptions);
    }

    @Operation(summary = "删除图表")
    @PostMapping("/deleteChart")
    public BaseResponse<Boolean> deleteChart(Long dashboardId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(dashboardService.deleteChart(dashboardId, loginUser));
    }

    @Operation(summary = "获取图表")
    @PostMapping("/getChartData")
    public BaseResponse<GetChartDataVO> getChartData(@Valid @RequestBody GetChartDataRequestDTO getChartDataRequestDTO, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(dashboardService.getChartData(getChartDataRequestDTO, loginUser));
    }
}
