package com.hwq.dataloom.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;

/**
 * @author HWQ
 * @date 2024/8/19 09:03
 * @description
 */
public enum DirTypeEnum {

    FILE("file", 0),
    DIR("dir", 1);

    DirTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }
    private final String text;

    private final Integer value;

    /**
     * 根据 text 获取枚举
     *
     * @param text
     * @return
     */
    public static DirTypeEnum getEnumByText(String text) {
        if (ObjectUtils.isEmpty(text)) {
            return null;
        }
        for (DirTypeEnum anEnum : DirTypeEnum.values()) {
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
    public static DirTypeEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (DirTypeEnum anEnum : DirTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
