package com.hwq.dataloom.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author:HWQ
 * @DateTime:2023/10/4 15:32
 * @Description:
 **/
public enum ChatHistoryStatusEnum {
    START("start", 0),
    ANALYSIS_COMPLETE("analysis_complete", 1),
    ANALYSIS_RELATE_TABLE_COMPLETE("analysis_relate_table_complete", 2),
    ALL_COMPLETE("all_complete", 3),
    END("end", 4),
    ERROR("error", 5);

    private final String text;

    private final Integer value;

    ChatHistoryStatusEnum(String text, Integer value) {
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
    public static ChatHistoryStatusEnum getEnumByValue(Integer value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ChatHistoryStatusEnum anEnum : ChatHistoryStatusEnum.values()) {
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
