package com.hwq.dataloom.core.workflow.graph_engine.entities;

import com.hwq.dataloom.core.workflow.variable.VariablePool;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author: HWQ
 * @Description: 画布运行状态
 * @DateTime: 2024/12/9 13:45
 **/
@Data
public class GraphRuntimeState {
    private VariablePool variablePool;

    private LocalDateTime startTime;

    private int totalToken;

    private Map<String, Object> outputs;

    private int nodeRunSteps;

    private RuntimeRouteState runtimeRouteState = new RuntimeRouteState();
}
