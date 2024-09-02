package com.hwq.dataloom.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author HWQ
 * @date 2024/8/30 17:19
 * @description 优惠券发送类型枚举类
 */
@RequiredArgsConstructor
public enum CouponSendTypeEnum {
    DIRECT(0, "直接发送"),
    DELAY(1, "定时发送");


    @Getter
    private final Integer type;

    @Getter
    private final String value;

    /**
     * 根据 type 找到对应的 value
     *
     * @param type 要查找的类型代码
     * @return 对应的描述值，如果没有找到抛异常
     */
    public static CouponSendTypeEnum findValueByType(int type) {
        for (CouponSendTypeEnum target : CouponSendTypeEnum.values()) {
            if (target.getType() == type) {
                return target;
            }
        }
        return null;
    }
}
