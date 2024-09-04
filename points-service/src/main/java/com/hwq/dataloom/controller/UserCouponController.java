package com.hwq.dataloom.controller;

import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.user_coupon.UserClaimCouponDTO;
import com.hwq.dataloom.service.UserCouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author HWQ
 * @date 2024/9/4 23:23
 * @description 用户优惠券
 */
@RestController
@RequestMapping("/points-service/userCoupon")
@Tag(name = "用户优惠券")
public class UserCouponController {

    @Resource
    private UserCouponService userCouponService;

    @Operation(summary = "领取优惠券")
    @PostMapping("/claim")
    public BaseResponse<Void> UserClaimCoupon(@RequestBody UserClaimCouponDTO requestParam) {
        userCouponService.userClaimCoupon(requestParam);
        return ResultUtils.success();
    }
}
