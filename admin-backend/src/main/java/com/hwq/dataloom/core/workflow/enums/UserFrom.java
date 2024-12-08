package com.hwq.dataloom.core.workflow.enums;

/**
 * @author HWQ
 * @date 2024/12/8 14:53
 * @description 调用用户类型
 */
public enum UserFrom {

    /**
     * 系统
     */
    ACCOUNT("account"),
    /**
     * 终端用户
     */
    END_USER("end_user")
    ;
    private final String value;

    UserFrom(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 模拟Python中value_of方法的功能，根据传入的值查找对应的枚举成员
    public static UserFrom fromValue(String value) {
        for (UserFrom type : UserFrom.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching enum found for value: " + value);
    }
}
