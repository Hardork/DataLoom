package com.hwq.dataloom.core.workflow.node.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * LLM节点数据类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LLMNodeData extends BaseNodeData {
    private Context context;
    private LLMModel llmModel;
    private List<PromptTemplate> promptTemplates;
    /**
     * 输出结果
     */
    private Output output;
}

/**
 * 模型上下文
 */
@Data
class Context {
    private Boolean enabled;
    private List<String> varSelector;
}

/**
 * LLM模型类
 */
@Data
class LLMModel {
    private String id;
    private String name;
    private String provider;
    private Map<String, String> config;
}

/**
 * Prompt模版
 */
@Data
class PromptTemplate {
    private String id;
    private String role;
    private String text;
}