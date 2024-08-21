package com.hwq.dataloom.model.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author HWQ
 * @date 2024/8/21 00:53
 * @description 数据源类型枚举类
 */
public enum DataSourceTypeEnum {

    MYSQL("mysql", "mysql"),
    API("api", "api"),
    EXCEL("excel", "excel");

    DataSourceTypeEnum(String text, String value){
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static DataSourceTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (DataSourceTypeEnum anEnum : DataSourceTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    private String text;
    private String value;

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
