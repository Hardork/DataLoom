package com.hwq.dataloom.model.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 工作流版本枚举类
 */
public enum WorkflowVersionEnum {
    // 草稿
    DRAFT("draft", "draft"),
    // 已发布
    PUBLISHED("published", "published");

    WorkflowVersionEnum(String text, String value){
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     * @param value
     * @return
     */
    public static WorkflowVersionEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (WorkflowVersionEnum anEnum : WorkflowVersionEnum.values()) {
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
