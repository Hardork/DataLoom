package com.hwq.dataloom.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author HWQ
 * @date 2024/8/30 17:27
 * @description 优惠券发放状态枚举类
 */
@AllArgsConstructor
public enum CouponTaskStatusEnum {

    WAIT_EXE(0, "待执行"),
    RUNNING(1, "执行中"),
    FAILED(2, "执行失败"),
    SUCCEED(3, "执行成功"),
    CANCELED(4, "取消");


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
    public static CouponTaskStatusEnum findValueByType(int type) {
        for (CouponTaskStatusEnum target : CouponTaskStatusEnum.values()) {
            if (target.getStatus() == type) {
                return target;
            }
        }
        return null;
    }
}
