package com.hwq.dataloom.model.enums.workflow;

import com.hwq.dataloom.core.workflow.node.answer.AnswerNodeData;
import com.hwq.dataloom.core.workflow.node.data.*;
import org.apache.commons.lang3.ObjectUtils;

/**
 * @author HWQ
 * @date 2024/11/22 16:39
 * @description 工作流节点类型枚举类
 */
public enum NodeTypeEnum {
    START("start", "start", BaseNodeData.class),
    DATA_EXTRACT("data_extract", "data_extract", DataExtractionNodeData.class),
    HTTP("http", "http", HttpNodeData.class),
    NOTIFY("notify", "notify", NotifyNodeData.class),
    CODE("code", "code", CodeNodeData.class),
    LLM("llm", "llm", LLMNodeData.class),
    ANSWER("answer", "answer", AnswerNodeData.class);

    NodeTypeEnum(String text, String value, Class<? extends BaseNodeData> clazz) {
        this.text = text;
        this.value = value;
        this.nodeClass = clazz;
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

    private final Class<? extends BaseNodeData> nodeClass;

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }

    public Class<? extends BaseNodeData> getNodeClass() {return nodeClass;}
}
