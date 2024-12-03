package com.hwq.dataloom.core.workflow.enums;

/**
 * @author HWQ
 * @date 2024/11/21 23:45
 * @description 变量类型枚举
 */
public enum SegmentType {
    NONE("none"),
    NUMBER("number"),
    STRING("string"),
    SECRET("secret"),
    ARRAY_ANY("array[any]"),
    ARRAY_STRING("array[string]"),
    ARRAY_NUMBER("array[number]"),
    ARRAY_OBJECT("array[object]"),
    OBJECT("object"),
    FILE("file"),
    ARRAY_FILE("array[file]"),
    GROUP("group");

    private final String value;

    SegmentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}