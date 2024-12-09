package com.hwq.dataloom.core.workflow.node.handler.test;

import com.hwq.dataloom.core.workflow.graph_engine.entities.GraphEngineEvent;
import com.hwq.dataloom.core.workflow.graph_engine.entities.NodeRunStartedEvent;
import com.hwq.dataloom.core.workflow.graph_engine.entities.RouteNodeState;
import jdk.nashorn.internal.ir.BaseNode;

import java.util.List;
import java.util.Optional;

public class GraphEngine {
    private GraphRuntimeState graph_runtime_state;

    public Seq<GraphEngineEvent> runNode(BaseNode node_instance, RouteNodeState route_node_state,
                                         Optional<String> parallel_id, Optional<String> parallel_start_node_id,
                                         Optional<String> parent_parallel_id, Optional<String> parent_parallel_start_node_id) {
        return () -> {
            // 触发节点运行开始事件，模拟生成并传递NodeRunStartedEvent事件，这里简化为打印输出，实际可能要放入队列等后续处理机制
            NodeRunStartedEvent startEvent = new NodeRunStartedEvent(
                    node_instance.getId(),
                    node_instance.getNode_id(),
                    node_instance.getNode_type(),
                    node_instance.getNode_data(),
                    route_node_state,
                    node_instance.getPrevious_node_id(),
                    parallel_id.orElse(null),
                    parallel_start_node_id.orElse(null),
                    parent_parallel_id.orElse(null),
                    parent_parallel_start_node_id.orElse(null)
            );
            System.out.println(startEvent);

            // 这里假设对应关闭数据库会话的操作，需要根据实际的数据库访问层来实现具体逻辑
            closeDbSession();

            try {
                Seq<GraphEngineEvent> generator = node_instance.run();
                boolean breakFlag = false;
                generator.consume(item -> {
                    if (item instanceof GraphEngineEvent) {
                        if (item instanceof BaseIterationEvent) {
                            BaseIterationEvent iterationEvent = (BaseIterationEvent) item;
                            iterationEvent.setParallel_id(parallel_id.orElse(null));
                            iterationEvent.setParallel_start_node_id(parallel_start_node_id.orElse(null));
                            iterationEvent.setParent_parallel_id(parent_parallel_id.orElse(null));
                            iterationEvent.setParent_parallel_start_node_id(parent_parallel_start_node_id.orElse(null));
                        }
                        System.out.println(item);
                    } else {
                        if (item instanceof RunCompletedEvent) {
                            RunResult run_result = ((RunCompletedEvent) item).getRun_result();
                            route_node_state.set_finished(run_result);
                            if (run_result.getStatus() == WorkflowNodeExecutionStatus.FAILED) {
                                NodeRunFailedEvent failedEvent = new NodeRunFailedEvent(
                                        route_node_state.getFailed_reason()!= null? route_node_state.getFailed_reason() : "Unknown error.",
                                        node_instance.getId(),
                                        node_instance.getNode_id(),
                                        node_instance.getNode_type(),
                                        node_instance.getNode_data(),
                                        route_node_state,
                                        parallel_id.orElse(null),
                                        parallel_start_node_id.orElse(null),
                                        parent_parallel_id.orElse(null),
                                        parent_parallel_start_node_id.orElse(null)
                                );
                                System.out.println(failedEvent);
                            } else if (run_result.getStatus() == WorkflowNodeExecutionStatus.SUCCEEDED) {
                                if (run_result.getMetadata()!= null && run_result.getMetadata().get(NodeRunMetadataKey.TOTAL_TOKENS)!= null) {
                                    graph_runtime_state.total_tokens += Integer.parseInt(run_result.getMetadata().get(NodeRunMetadataKey.TOTAL_TOKENS).toString());
                                }
                                if (run_result.getLlm_usage()!= null) {
                                    graph_runtime_state.llm_usage += run_result.getLlm_usage();
                                }
                                if (run_result.getOutputs()!= null) {
                                    for (String variable_key : run_result.getOutputs().keySet()) {
                                        Object variable_value = run_result.getOutputs().get(variable_key);
                                        // 模拟递归添加变量的方法，这里简化，实际要按真实逻辑实现_recursively方法
                                        _append_variables_recursively(node_instance.getNode_id(), List.of(variable_key), variable_value);
                                    }
                                }
                                if (parallel_id.isPresent() && parallel_start_node_id.isPresent()) {
                                    if (run_result.getMetadata() == null) {
                                        run_result.setMetadata(new HashMap<>());
                                    }
                                    run_result.getMetadata().put(NodeRunMetadataKey.PARALLEL_ID, parallel_id.get());
                                    run_result.getMetadata().put(NodeRunMetadataKey.PARALLEL_START_NODE_ID, parallel_start_node_id.get());
                                    if (parent_parallel_id.isPresent() && parent_parallel_start_node_id.isPresent()) {
                                        run_result.getMetadata().put(NodeRunMetadataKey.PARENT_PARALLEL_ID, parent_parallel_id.get());
                                        run_result.getMetadata().put(NodeRunMetadataKey.PARENT_PARALLEL_START_NODE_ID, parent_parallel_start_node_id.get());
                                    }
                                }
                                NodeRunSucceededEvent succeededEvent = new NodeRunSucceededEvent(
                                        node_instance.getId(),
                                        node_instance.getNode_id(),
                                        node_instance.getNode_type(),
                                        node_instance.getNode_data(),
                                        route_node_state,
                                        parallel_id.orElse(null),
                                        parallel_start_node_id.orElse(null),
                                        parent_parallel_id.orElse(null),
                                        parent_parallel_start_node_id.orElse(null)
                                );
                                System.out.println(succeededEvent);
                            }
                            breakFlag = true;
                        } else if (item instanceof RunStreamChunkEvent) {
                            NodeRunStreamChunkEvent streamChunkEvent = new NodeRunStreamChunkEvent(
                                    node_instance.getId(),
                                    node_instance.getNode_id(),
                                    node_instance.getNode_type(),
                                    node_instance.getNode_data(),
                                    ((RunStreamChunkEvent) item).getChunk_content(),
                                    ((RunStreamChunkEvent) item).getFrom_variable_selector(),
                                    route_node_state,
                                    parallel_id.orElse(null),
                                    parallel_start_node_id.orElse(null),
                                    parent_parallel_id.orElse(null),
                                    parent_parallel_start_node_id.orElse(null)
                            );
                            System.out.println(streamChunkEvent);
                        } else if (item instanceof RunRetrieverResourceEvent) {
                            NodeRunRetrieverResourceEvent retrieverResourceEvent = new NodeRunRetrieverResourceEvent(
                                    node_instance.getId(),
                                    node_instance.getNode_id(),
                                    node_instance.getNode_type(),
                                    node_instance.getNode_data(),
                                    ((RunRetrieverResourceEvent) item).getRetriever_resources(),
                                    ((RunRetrieverResourceEvent) item).getContext(),
                                    route_node_state,
                                    parallel_id.orElse(null),
                                    parallel_start_node_id.orElse(null),
                                    parent_parallel_id.orElse(null),
                                    parent_parallel_start_node_id.orElse(null)
                            );
                            System.out.println(retrieverResourceEvent);
                        }
                    }
                });
                if (breakFlag) {
                    return;
                }
            } catch (GenerateTaskStoppedError e) {
                route_node_state.setStatus(RouteNodeState.Status.FAILED);
                route_node_state.setFailed_reason("Workflow stopped.");
                NodeRunFailedEvent failedEvent = new NodeRunFailedEvent(
                        "Workflow stopped.",
                        node_instance.getId(),
                        node_instance.getNode_id(),
                        node_instance.getNode_type(),
                        node_instance.getNode_data(),
                        route_node_state,
                        parallel_id.orElse(null),
                        parallel_start_node_id.orElse(null),
                        parent_parallel_id.orElse(null),
                        parent_parallel_start_node_id.orElse(null)
                );
                System.out.println(failedEvent);
                return;
            } catch (Exception e) {
                // 这里假设存在对应的日志记录工具类来记录异常，模拟Python中的logger.exception
                LoggerUtil.exception("Node " + node_instance.getNode_data().getTitle() + " run failed: " + e.getMessage());
                throw e;
            } finally {
                closeDbSession();
            }
        };
    }

    private void closeDbSession() {
        // 实际实现数据库会话关闭的具体逻辑，这里暂时为空方法，需根据实际情况完善
    }

    private void _append_variables_recursively(String node_id, List<String> variable_key_list, Object variable_value) {
        // 模拟递归添加变量的逻辑，根据实际业务需求完善具体实现
    }
}