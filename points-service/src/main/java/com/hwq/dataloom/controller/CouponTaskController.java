package com.hwq.dataloom.controller;

import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.coupon_task.CouponTaskCreateReqDTO;
import com.hwq.dataloom.service.CouponTaskService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author HWQ
 * @date 2024/8/27 12:10
 * @description 优惠券发放任务
 */
@RestController
@RequestMapping("/couponTask")
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
}
