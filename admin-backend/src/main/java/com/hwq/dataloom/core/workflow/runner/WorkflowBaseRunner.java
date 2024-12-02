package com.hwq.dataloom.core.workflow.runner;
import java.util.*;

import cn.hutool.core.lang.Pair;
import com.hwq.dataloom.core.workflow.entitys.SingleIterationRunEntity;
import com.hwq.dataloom.core.workflow.entitys.variable.VariablePool;
import com.hwq.dataloom.core.workflow.entitys.WorkflowGenerateEntity;
import com.hwq.dataloom.core.workflow.queue.WorkflowQueueManager;
import com.hwq.dataloom.model.entity.Workflow;

import com.hwq.dataloom.model.json.workflow.Graph;
import org.springframework.stereotype.Component;

/**
 * @author HWQ
 * @date 2024/11/21 23:45
 * @description 工作流运行基类
 */
@Component
public class WorkflowBaseRunner {


    // TODO: 初始化Graph，包含寻找Start节点、
    public void run(WorkflowGenerateEntity workflowGenerateEntity, WorkflowQueueManager workflowQueueManager, Workflow workflow) {

        SingleIterationRunEntity singleIterationRunEntity = workflowGenerateEntity.getSingleIterationRunEntity();
        if (singleIterationRunEntity != null) {

            Pair<Graph, VariablePool> graphVariablePoolPair = getGraphAndVariablePoolOfSingleIteration(workflow, singleIterationRunEntity.getNodeId(), workflowGenerateEntity.getInputs());
        } else {
            Graph graph = Graph.init(workflow.getGraph(), null);
        }

        // init graph

        // init end stream param

    }

    private Pair<Graph, VariablePool> getGraphAndVariablePoolOfSingleIteration(Workflow workflow, String nodeId, Map<String, Object> inputs) {
        return null;
    }



}
