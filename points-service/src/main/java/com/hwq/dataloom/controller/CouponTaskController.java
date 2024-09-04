package com.hwq.dataloom.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.coupon_task.CouponTaskCreateReqDTO;
import com.hwq.dataloom.model.dto.coupon_task.CouponTaskPageQueryReqDTO;
import com.hwq.dataloom.model.entity.CouponTask;
import com.hwq.dataloom.model.vo.CouponTaskPageQueryRespDTO;
import com.hwq.dataloom.service.CouponTaskService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author HWQ
 * @date 2024/8/27 12:10
 * @description 优惠券发放任务
 */
@RestController
@RequestMapping("/points-service/couponTask")
public class CouponTaskController {


    @Resource
    private CouponTaskService couponTaskService;

    /**
     * 手动上传优惠券发放任务
     * @param couponTaskCreateReqDTO 创建优惠券发放请求类
     * @return void
     */
    @PostMapping("/manual/send")
    public BaseResponse<Void> manuallySendCouponTask(@RequestBody CouponTaskCreateReqDTO couponTaskCreateReqDTO) {
        couponTaskService.manuallySendCouponTask(couponTaskCreateReqDTO);
        return ResultUtils.success();
    }

    @Operation(summary = "分页查询优惠券推送任务")
    @GetMapping("/page")
    public BaseResponse<Page<CouponTask>> pageQueryCouponTask(CouponTaskPageQueryReqDTO requestParam) {
        return ResultUtils.success(couponTaskService.pageQueryCouponTask(requestParam));
    }

    @Operation(summary = "查询优惠券推送任务详情")
    @GetMapping("/find")
    public BaseResponse<CouponTask> findCouponTaskById(Long taskId) {
        return ResultUtils.success(couponTaskService.getById(taskId));
    }
}
