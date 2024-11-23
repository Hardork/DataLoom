package com.hwq.dataloom.model.enums.workflow;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author HWQ
 * @date 2024/11/22 16:39
 * @description 工作流节点类型枚举类
 */
public enum NodeTypeEnum {
    START("start", "start"),
    DATA_EXTRACT("data_extract", "data_extract"),
    HTTP("http", "http"),
    NOTIFY("notify", "notify"),
    CODE("code", "code"),
    LLM("llm", "llm");

    NodeTypeEnum(String text, String value){
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     * @param value
     * @return
     */
    public static NodeTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (NodeTypeEnum anEnum : NodeTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    private final String text;
    private final String value;

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
