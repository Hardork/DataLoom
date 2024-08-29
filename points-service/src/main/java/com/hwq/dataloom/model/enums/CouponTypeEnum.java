package com.hwq.dataloom.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author HWQ
 * @date 2024/8/29 00:17
 * @description 优惠券类型枚举类
 */
@RequiredArgsConstructor
public enum CouponTypeEnum {
    BUY_POINT_DISCOUNT(0, "积分购买折扣券"),
    SERVICE_EXPERIENCE(1, "服务体验券"),
    SERVICE_DISCOUNT(2, "服务积分打折");


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
    public static CouponTypeEnum findValueByType(int type) {
        for (CouponTypeEnum target : CouponTypeEnum.values()) {
            if (target.getType() == type) {
                return target;
            }
        }
        return null;
    }
}
