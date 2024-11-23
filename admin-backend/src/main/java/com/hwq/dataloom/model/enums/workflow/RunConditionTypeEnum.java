package com.hwq.dataloom.model.enums.workflow;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author HWQ
 * @date 2024/11/23 17:54
 * @description 运行条件类型枚举
 */
public enum RunConditionTypeEnum {
    // 分支判断
    BRANCH_IDENTIFY("branch_identify", "branch_identify"),
    // 条件判断
    CONDITION("condition", "condition");


    RunConditionTypeEnum(String text, String value){
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     * @param value
     * @return
     */
    public static RunConditionTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (RunConditionTypeEnum anEnum : RunConditionTypeEnum.values()) {
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