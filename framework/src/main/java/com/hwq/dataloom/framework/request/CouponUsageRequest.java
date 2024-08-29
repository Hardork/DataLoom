package com.hwq.dataloom.framework.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author HWQ
 * @date 2024/8/28 17:40
 * @description 优惠券使用请求通用类
 */
@Data
public class CouponUsageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 对应的优惠券ID
     */
    private Long couponId;

}
