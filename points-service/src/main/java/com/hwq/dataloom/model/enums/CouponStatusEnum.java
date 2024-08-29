package com.hwq.dataloom.model.enums;

import lombok.*;

/**
 * @author HWQ
 * @date 2024/8/29 13:43
 * @description
 */
@RequiredArgsConstructor
public enum CouponStatusEnum {
    ONLINE(0, "使用中"),
    OFFLINE(1, "已下线");


    @Getter
    private final Integer status;

    @Getter
    private final String value;

    /**
     * 根据 type 找到对应的 value
     *
     * @param type 要查找的类型代码
     * @return 对应的描述值，如果没有找到抛异常
     */
    public static CouponStatusEnum findValueByType(int type) {
        for (CouponStatusEnum target : CouponStatusEnum.values()) {
            if (target.getStatus() == type) {
                return target;
            }
        }
        return null;
    }
}
