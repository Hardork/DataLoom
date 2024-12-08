package com.hwq.dataloom.core.workflow.entitys;

import com.hwq.dataloom.core.model_runtime.entity.LLMUsage;
import com.hwq.dataloom.core.workflow.enums.FileType;
import com.hwq.dataloom.core.workflow.enums.NodeRunMetadataKey;
import com.hwq.dataloom.core.workflow.enums.NodeRunStatus;
import lombok.Data;

import java.util.Map;

/**
 * @author HWQ
 * @date 2024/12/7 19:27
 * @description 节点运行结果类
 */
@Data
public class NodeRunResult {

    /**
     * 节点运行状态
     */
    private NodeRunStatus status = NodeRunStatus.RUNNING;

    /**
     * 节点输入参数
     */
    private Map<String, Object> inputs;

    /**
     * 元数据
     */
    private Map<NodeRunMetadataKey, Object> metadata;

    /**
     * 当前节点使用模型计费
     */
    private LLMUsage llmUsage;

    /**
     * 多分支源节点ID
     */
    private String edgeSourceHandle;

    /**
     * 报错信息，如果发生错误
     */
    private String error;

}
