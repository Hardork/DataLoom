package com.hwq.dataloom.core.workflow.enums;

public enum VariableEntityType {
    TEXT_INPUT("text-input"),
    SELECT("select"),
    PARAGRAPH("paragraph"),
    NUMBER("number"),
    EXTERNAL_DATA_TOOL("external_data_tool"),
    FILE("file"),
    FILE_LIST("file-list");

    private final String value;

    VariableEntityType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 可以添加一个根据值获取对应枚举实例的静态方法，方便使用
    public static VariableEntityType fromValue(String value) {
        for (VariableEntityType type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid VariableEntityType value: " + value);
    }
}