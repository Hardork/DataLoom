package com.hwq.dataloom.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author HWQ
 * @date 2024/8/30 17:31
 * @description 优惠券发放后通知方式枚举类
 */
@RequiredArgsConstructor
public enum CouponNotifyTypeEnum {
    WEB_NOTIFY(0, "站内通知"),
    EMAIL_NOTIFT(1, "邮箱通知"),
    SMS_NOTIFY(2, "短信通知");


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
    public static CouponNotifyTypeEnum findValueByType(int type) {
        for (CouponNotifyTypeEnum target : CouponNotifyTypeEnum.values()) {
            if (target.getType() == type) {
                return target;
            }
        }
        return null;
    }
}
