package com.hwq.dataloom.core.workflow.graph_engine;

import com.hwq.dataloom.core.workflow.enums.UserFrom;
import com.hwq.dataloom.core.workflow.graph.Graph;
import com.hwq.dataloom.core.workflow.graph.GraphRunEntity;
import com.hwq.dataloom.core.workflow.variable.VariablePool;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author HWQ
 * @date 2024/12/17 23:11
 * @description 画布运行引擎
 */
@Data
public class GraphEngine {
//    workflow_thread_pool_mapping: dict[str, GraphEngineThreadPool] = {}
    private Long workflowId;

    private Long userId;

    private UserFrom userFrom;

    private int callDepth;

    private GraphRunEntity graphRunEntity;

    private Graph graph;

    private VariablePool variablePool;

    private int maxExecutionSteps;

    private int maxExecutionTime;

    private String threadPoolId;

    public GraphEngine (
            Long workflowId,
            Long userId,
            UserFrom userFrom,
            int callDepth,
            GraphRunEntity graphRunEntity,
            Graph graph,
            VariablePool variablePool,
            int maxExecutionSteps,
            int maxExecutionTime,
            String threadPoolId
    ) {
        this.workflowId = workflowId;
        this.userId = userId;
        this.userFrom = userFrom;
        this.callDepth = callDepth;
        this.graphRunEntity = graphRunEntity;
        this.graph = graph;
        this.variablePool = variablePool;
        this.maxExecutionSteps = maxExecutionSteps;
        this.maxExecutionTime = maxExecutionTime;
        this.threadPoolId = threadPoolId;

        int threadPoolMaxSubmitCount = 10;
        int threadPoolMaxWorkers = 10;

        if (StringUtils.isNotEmpty(threadPoolId)) {
            // TODO: threadPoolId是否存在于线程池集合中
        }
    }


}
