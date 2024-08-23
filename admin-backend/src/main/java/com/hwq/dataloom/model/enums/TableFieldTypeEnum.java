package com.hwq.dataloom.model.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author HWQ
 * @date 2024/8/23 10:40
 * @description 数据库字段枚举类
 */
public enum TableFieldTypeEnum {
    TEXT("text", "text"),
    DATETIME("datetime", "datetime"),

    BIGINT("bigint", "bigint"),
    DOUBLE("double", "double");

    TableFieldTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
    private final String text;

    private final String value;

    /**
     * 根据 text 获取枚举
     *
     * @param text
     * @return
     */
    public static TableFieldTypeEnum getEnumByText(String text) {
        if (ObjectUtils.isEmpty(text)) {
            return null;
        }
        for (TableFieldTypeEnum anEnum : TableFieldTypeEnum.values()) {
            if (anEnum.getText().equals(text)) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static TableFieldTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (TableFieldTypeEnum anEnum : TableFieldTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
