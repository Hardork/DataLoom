package com.hwq.dataloom.utils;

/**
 * @author HWQ
 * @date 2024/9/1 17:02
 * @description Lua脚本结果处理工具类
 */
public class StockDecrementReturnCombinedUtil {

    /**
     * 2^13 > 5000, 所以用 13 位来表示第二个字段
     */
    private static final int SECOND_FIELD_BITS = 13;

    /**
     * 2^14 > 9999, 所以用 14 位来表示第二个字段
     */
    private static final int USER_CLAIM_COUPON_SECOND_FIELD_BITS = 14;

    /**
     * 将两个字段组合成一个int
     */
    public static int combineFields(boolean decrementFlag, int userRecord) {
        return (decrementFlag ? 1 : 0) << SECOND_FIELD_BITS | userRecord;
    }

    /**
     * 从组合的int中提取第一个字段（0或1）
     */
    public static boolean extractFirstField(long combined) {
        return (combined >> SECOND_FIELD_BITS) != 0;
    }

    /**
     * 从组合的int中提取第二个字段（1到5000之间的数字）
     */
    public static int extractSecondField(int combined) {
        return combined & ((1 << SECOND_FIELD_BITS) - 1);
    }

    /**
     * 用户领取优惠券lua脚本
     * 从组合的 int 中提取第一个字段（0、1或2）
     */
    public static long extractUserClaimCouponFirstField(long combined) {
        return (combined >> USER_CLAIM_COUPON_SECOND_FIELD_BITS) & 0b11; // 0b11 即二进制的 11，用于限制结果为 2 位
    }

    /**
     * 用户领取优惠券lua脚本
     * 从组合的 int 中提取第二个字段（0 到 9999 之间的数字）
     */
    public static long extractUserClaimCouponSecondField(long combined) {
        return combined & ((1 << USER_CLAIM_COUPON_SECOND_FIELD_BITS) - 1);
    }


}