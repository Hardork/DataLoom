package com.hwq.dataloom.core.workflow.enums;

/**
 * @Author: HWQ
 * @Description: 块类型
 * @DateTime: 2024/12/12 17:04
 **/
public enum ChunkType {
    /**
     * 变量类型
     */
    VAR("var"),
    /**
     * 文本类型
     */
    TEXT("text");

    private final String value;

    ChunkType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 根据传入的值查找对应的枚举成员
    public static ChunkType fromValue(String value) {
        for (ChunkType type : ChunkType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching enum found for value: " + value);
    }
}
