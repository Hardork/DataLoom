package com.hwq.dataloom.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author HWQ
 * @date 2024/9/2 02:53
 * @description
 */
@RequiredArgsConstructor
public enum UserCouponStatusEnum {
    UNUSED(0, "未使用"),
    USED(1, "已使用"),
    EXPIRED(2, "过期");


    @Getter
    private final Integer status;

    @Getter
    private final String value;

    /**
     * 根据 type 找到对应的 value
     *
     * @param status 要查找的类型代码
     * @return 对应的描述值，如果没有找到抛异常
     */
    public static UserCouponStatusEnum findValueByType(int status) {
        for (UserCouponStatusEnum target : UserCouponStatusEnum.values()) {
            if (target.getStatus() == status) {
                return target;
            }
        }
        return null;
    }
}
