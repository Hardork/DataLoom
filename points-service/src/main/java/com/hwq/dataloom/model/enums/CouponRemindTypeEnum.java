package com.hwq.dataloom.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author HWQ
 * @date 2024/9/4 03:10
 * @description
 */
@RequiredArgsConstructor
public enum CouponRemindTypeEnum {

    /**
     * 邮件提醒
     */
    EMAIL(0, "邮件提醒"),
    MESSAGE(1, "短信提醒");

    @Getter
    private final int type;
    @Getter
    private final String describe;

    public static CouponRemindTypeEnum getByType(Integer type) {
        for(CouponRemindTypeEnum remindEnum : values()){
            if (remindEnum.getType() == type) {
                return remindEnum;
            }
        }
        return null;
    }

    public static String getDescribeByType(Integer type) {
        for(CouponRemindTypeEnum remindEnum : values()){
            if (remindEnum.getType() == type) {
                return remindEnum.getDescribe();
            }
        }
        return null;
    }
}