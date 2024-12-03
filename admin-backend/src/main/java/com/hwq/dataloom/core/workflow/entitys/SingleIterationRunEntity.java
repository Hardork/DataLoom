package com.hwq.dataloom.core.workflow.entitys;

import lombok.Data;

import java.util.Map;

/**
 * @author HWQ
 * @date 2024/12/2 16:17
 * @description 指定节点运行实体
 */
@Data
public class SingleIterationRunEntity {
    private String nodeId;

    private Map<String, Object> inputs;
}
