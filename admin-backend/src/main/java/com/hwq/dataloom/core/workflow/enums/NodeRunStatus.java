package com.hwq.dataloom.core.workflow.enums;

/**
 * @author HWQ
 * @date 2024/12/7 19:28
 * @description 节点运行状态枚举
 */
public enum NodeRunStatus {
    SUCCESS("success"),
    FAILED("failed"),
    RUNNING("running");

    private final String value;

    NodeRunStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 根据传入的值查找对应的枚举成员
    public static NodeRunStatus fromValue(String value) {
        for (NodeRunStatus type : NodeRunStatus.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching enum found for value: " + value);
    }
}
