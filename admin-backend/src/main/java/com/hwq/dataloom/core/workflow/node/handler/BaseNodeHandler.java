package com.hwq.dataloom.core.workflow.node.handler;

import com.hwq.dataloom.core.workflow.entitys.NodeRunResult;
import com.hwq.dataloom.core.workflow.enums.NodeRunStatus;
import com.hwq.dataloom.core.workflow.enums.UserFrom;
import com.hwq.dataloom.core.workflow.graph.Graph;
import com.hwq.dataloom.core.workflow.graph_engine.GraphInitParams;
import com.hwq.dataloom.core.workflow.graph_engine.entities.GraphRuntimeState;
import com.hwq.dataloom.core.workflow.graph_engine.entities.event.BaseNodeEvent;
import com.hwq.dataloom.core.workflow.node.Node;
import com.hwq.dataloom.core.workflow.node.event.RunCompletedEvent;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.utils.generator.Generator;
import com.hwq.dataloom.utils.generator.Seq;
import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.events.NodeEvent;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @Author: HWQ
 * @Description: 节点处理器基类
 * @DateTime: 2024/12/6 18:00
 **/
@Data
@Slf4j
public abstract class BaseNodeHandler {

    private NodeTypeEnum nodeTypeEnum;

    private String id;

    private Map<String, Object> config;

    private GraphInitParams graphInitParams;

    private GraphRuntimeState graphRuntimeState;

    private Graph graph;

    private String threadPoolId;

    private Long workflowId;

    private UserFrom userFrom;

    private int callDepth;

    private String previousNodeId;

    private Long userId;

    private String nodeId;


    public BaseNodeHandler(){}

    public BaseNodeHandler(
            NodeTypeEnum nodeTypeEnum,
            String id, Map<String, Object> config,
            Graph graph,
            GraphInitParams graphInitParams,
            GraphRuntimeState graphRuntimeState,
            String previousNodeId,
            String threadPoolId
    ) {
        this.nodeTypeEnum = nodeTypeEnum;
        this.id = id;
        this.workflowId = graphInitParams.getWorkflowId();
        this.config = graphInitParams.getGraphConfig();
        this.userId = graphInitParams.getUserId();
        this.userFrom = graphInitParams.getUserFrom();
        this.callDepth = graphInitParams.getCallDepth();
        this.graph = graph;
        this.graphRuntimeState = graphRuntimeState;
        this.previousNodeId = previousNodeId;
        this.threadPoolId = threadPoolId;

        String nodeId = (String) config.get("id");
        ThrowUtils.throwIf(nodeId == null, ErrorCode.OPERATION_ERROR, "Node id 不得为空");
        this.nodeId = nodeId;

    }

    /**
     * 提取变量到Map中
     * @param graph 图
     * @param node 当前节点
     * @return Map
     */
    public abstract Map<String, List<String>> extractVariableSelectorToVariableMapping(Graph graph, Node node);


    /**
     * 节点运行
     * @return 结果流
     */
    public Stream<Object> run() {
        Object nodeRunResult;
        try {
            nodeRunResult = this.nodeRun();
        } catch (Exception e) {
            log.error("node {} run failed: ", id, e);
            nodeRunResult = new NodeRunResult(NodeRunStatus.FAILED, e.getMessage());
        }
        Object finalNodeRunResult = nodeRunResult;
        return Generator.stream(c -> {
            if (finalNodeRunResult != null) {
                c.accept(new RunCompletedEvent((NodeRunResult) finalNodeRunResult));
            }
            if (finalNodeRunResult instanceof Stream) {
                ((Stream<?>) finalNodeRunResult).forEach(c);
            }
        });
    }



    public abstract NodeRunResult nodeRun();
}
