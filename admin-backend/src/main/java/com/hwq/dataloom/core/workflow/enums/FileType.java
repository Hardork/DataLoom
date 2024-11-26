package com.hwq.dataloom.core.workflow.enums;

/**
 * 文件类型枚举类
 */
public enum FileType {
    IMAGE("image"),
    DOCUMENT("document"),
    AUDIO("audio"),
    VIDEO("video"),
    CUSTOM("custom");

    private final String value;

    FileType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 模拟Python中value_of方法的功能，根据传入的值查找对应的枚举成员
    public static FileType fromValue(String value) {
        for (FileType type : FileType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching enum found for value: " + value);
    }
}