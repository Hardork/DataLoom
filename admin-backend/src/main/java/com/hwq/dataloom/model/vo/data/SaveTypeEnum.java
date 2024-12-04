package com.hwq.dataloom.model.vo.data;

import com.hwq.dataloom.framework.model.enums.WebSocketMsgTypeEnum;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/29
 * @Description:
 **/
public enum SaveTypeEnum {
    sql("sql", 0),
    data("data", 1);

    private final String text;

    private final Integer value;

    SaveTypeEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static SaveTypeEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (SaveTypeEnum anEnum : SaveTypeEnum.values()) {
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
