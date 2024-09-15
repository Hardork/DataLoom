package com.hwq.dataloom.model.enums;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HWQ
 * @date 2024/9/16 00:46
 * @description 数值列查询类型枚举类
 */
public enum SeriesArrayRollUpEnum {
    COUNT("记录数", "COUNT", "COUNT(%s) AS %s"),
    SUM("求和", "SUM", "SUM(%s) AS %s"),
    MAX("最大值", "MAX", "MAX(%s) AS %s"),
    MIN("最小值", "MIN", "MIN(%s) AS %s"),
    AVERAGE("平均值", "AVERAGE", "AVG(%s) AS %s");

    private final String text;

    private final String value;

    private final String selectTemplate;

    SeriesArrayRollUpEnum(String text, String value, String selectTemplate) {
        this.text = text;
        this.value = value;
        this.selectTemplate = selectTemplate;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static SeriesArrayRollUpEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (SeriesArrayRollUpEnum anEnum : SeriesArrayRollUpEnum.values()) {
            if (StringUtils.equalsIgnoreCase(value, anEnum.value)) {
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

    public String getSelectTemplate() {
        return selectTemplate;
    }
}
