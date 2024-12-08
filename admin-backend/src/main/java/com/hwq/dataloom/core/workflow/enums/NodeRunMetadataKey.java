package com.hwq.dataloom.core.workflow.enums;

/**
 * @author HWQ
 * @date 2024/12/7 22:15
 * @description 节点运行元数据key
 */
public enum NodeRunMetadataKey {
    TOTAL_TOKENS("total_tokens"),
    TOTAL_PRICE("total_price"),
    TOOL_INFO("tool_info"),
    ITERATION_ID("iteration_id"),
    ITERATION_INDEX("iteration_index"),
    PARALLEL_ID("parallel_id"),
    PARENT_PARALLEL_ID("parent_parallel_id"),
    PARENT_PARALLEL_START_NODE_ID("parent_parallel_start_node_id"),
    ;

    private final String value;

    NodeRunMetadataKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    // 模拟Python中value_of方法的功能，根据传入的值查找对应的枚举成员
    public static NodeRunMetadataKey fromValue(String value) {
        for (NodeRunMetadataKey type : NodeRunMetadataKey.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching enum found for value: " + value);
    }
}
