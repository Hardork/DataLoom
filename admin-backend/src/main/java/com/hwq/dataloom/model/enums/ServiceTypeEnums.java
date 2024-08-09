package com.hwq.dataloom.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author:HWQ
 * @DateTime:2023/9/29 16:08
 * @Description:
 **/
public enum ServiceTypeEnums {
    BI("BI", 0l),
    AI("AI", 1l);

    private final String text;

    private final Long value;

    ServiceTypeEnums(String text, Long value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Long> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static ServiceTypeEnums getEnumByValue(Long value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ServiceTypeEnums anEnum : ServiceTypeEnums.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Long getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
