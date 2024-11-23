package com.hwq.dataloom.model.enums.workflow;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author HWQ
 * @date 2024/11/23 17:34
 * @description 比较运算枚举类
 */
public enum ComparisonOperatorEnum {
    /**
     * for string
     */
    CONTAINS("contains", "contains"),
    NOT_CONTAINS("not_contains", "not_contains"),
    START_WITH("start_with", "start_with"),
    END_WITH("end_with", "end_with"),
    IS("is", "is"),
    EMPTY("empty", "empty"),
    NOT_EMPTY("not_empty", "not_empty"),
    IN("in", "in"),
    NOT_IN("not_in", "not_in"),
    /**
     * for number
     */
    NUMBER_EQUAL("=", "="),
    NUMBER_NOT_EQUAL("≠", "≠"),
    NUMBER_GT(">", ">"),
    NUMBER_LT("<", "<"),
    NUMBER_GT_OR_EQUAL("≥", "≥"),
    NUMBER_LT_OR_EQUAL("≤", "≤"),
    NUMBER_NULL("null", "null"),
    NUMBER_NOT_NULL("not_null", "not_null"),
    /**
     * for file
     */
    EXISTS("exists", "exists"),
    NOT_EXISTS("not_exists", "not_exists")
    ;

    ComparisonOperatorEnum(String text, String value){
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     * @param value
     * @return
     */
    public static ComparisonOperatorEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ComparisonOperatorEnum anEnum : ComparisonOperatorEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    private final String text;
    private final String value;

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
