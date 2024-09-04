package com.hwq.dataloom.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author HWQ
 * @date 2024/9/5 03:04
 * @description 执行用户领取优惠券lua脚本返回结果枚举
 */
@RequiredArgsConstructor
public enum RedisUserClaimCouponStatusEnum {
    /**
     * 成功
     */
    SUCCESS(0, "成功"),

    /**
     * 库存不足
     */
    STOCK_INSUFFICIENT(1, "优惠券已被领取完啦"),

    /**
     * 用户已经达到领取上限
     */
    LIMIT_REACHED(2, "用户已经达到领取上限");

    @Getter
    private final long status;
    @Getter
    private final String message;

    /**
     * 根据 code 找到对应的枚举实例判断是否成功标识
     *
     * @param code 要查找的编码
     * @return 是否成功标识
     */
    public static boolean isFail(long code) {
        for (RedisUserClaimCouponStatusEnum status : RedisUserClaimCouponStatusEnum.values()) {
            if (status.status == code) {
                return status != SUCCESS;
            }
        }
        return false;
    }

    /**
     * 根据 type 找到对应的枚举实例
     *
     * @param code 要查找的编码
     * @return 对应的枚举实例
     */
    public static String fromType(long code) {
        for (RedisUserClaimCouponStatusEnum method : RedisUserClaimCouponStatusEnum.values()) {
            if (method.getStatus() == code) {
                return method.getMessage();
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }
}
