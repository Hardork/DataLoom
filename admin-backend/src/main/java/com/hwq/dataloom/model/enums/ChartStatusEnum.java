package com.hwq.dataloom.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author:HWQ
 * @DateTime:2023/9/17 19:05
 * @Description: wait,running,succeed,failed
 **/
public enum ChartStatusEnum {
    WAIT("等待中", "wait"),
    RUNNING("进行中", "running"),
    SUCCEED("成功", "succeed"),
    TIMEOUT("超时", "timeout"),
    FAILED("失败", "failed");

    private final String text;

    private final String value;

    ChartStatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
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
    public static ChartStatusEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ChartStatusEnum anEnum : ChartStatusEnum.values()) {
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
