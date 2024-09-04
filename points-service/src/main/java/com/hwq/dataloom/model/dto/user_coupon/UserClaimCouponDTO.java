package com.hwq.dataloom.model.dto.user_coupon;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author HWQ
 * @date 2024/9/5 01:20
 * @description 用户领取优惠券请求类
 */
@Data
public class UserClaimCouponDTO {
    /**
     * 优惠券id
     */
    @NotNull(message = "优惠券id不得为空")
    private Long couponTemplateId;
}
