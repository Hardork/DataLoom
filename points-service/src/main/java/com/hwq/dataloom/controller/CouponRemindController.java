package com.hwq.dataloom.controller;

import com.hwq.dataloom.annotation.NoRepeatSubmit;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.coupon_remind.CouponTemplateRemindCreateReqDTO;
import com.hwq.dataloom.service.CouponTemplateRemindService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HWQ
 * @date 2024/9/3 02:13
 * @description 优惠券预约接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/CouponRemind")
@Tag(name = "优惠券预约提醒管理")
public class CouponRemindController {
    // TODO：优惠券预约
    private final CouponTemplateRemindService couponTemplateRemindService;

    @Operation(summary = "发出优惠券预约提醒请求")
    @NoRepeatSubmit(message = "请勿短时间内重复提交预约提醒请求")
    @PostMapping("/create")
    public BaseResponse<Boolean> createCouponRemind(@RequestBody CouponTemplateRemindCreateReqDTO requestParam) {
        return ResultUtils.success(couponTemplateRemindService.createCouponRemind(requestParam));
    }
}
