package com.hwq.dataloom.model.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author HWQ
 * @date 2024/11/18 22:23
 * @description 工作流类型枚举类
 */
public enum WorkflowTypeEnum {
    WORKFLOW("workflow", "workflow");

    WorkflowTypeEnum(String text, String value){
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     * @param value
     * @return
     */
    public static WorkflowTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (WorkflowTypeEnum anEnum : WorkflowTypeEnum.values()) {
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
