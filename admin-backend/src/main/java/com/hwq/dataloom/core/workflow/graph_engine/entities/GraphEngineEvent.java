package com.hwq.dataloom.core.workflow.graph_engine.entities;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hwq.dataloom.core.workflow.node.data.BaseNodeData;
import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;
import lombok.Getter;
import lombok.Setter;

abstract class GraphEngineEvent {
}

class BaseGraphEvent extends GraphEngineEvent {
}

class GraphRunStartedEvent extends BaseGraphEvent {
}

class GraphRunSucceededEvent extends BaseGraphEvent {

    @JsonProperty("outputs")
    private Map<String, Object> outputs;

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, Object> outputs) {
        this.outputs = outputs;
    }
}

class GraphRunFailedEvent extends BaseGraphEvent {

    @JsonProperty("error")
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

class BaseNodeEvent extends GraphEngineEvent {

    // Getters and setters
    @Getter
    @Setter
    @JsonProperty("id")
    private String id;

    @JsonProperty("node_id")
    private String nodeId;

    @JsonProperty("node_type")
    private NodeTypeEnum nodeType;

    @JsonProperty("node_data")
    private BaseNodeData nodeData;

    @JsonProperty("route_node_state")
    private RouteNodeState routeNodeState;

    @JsonProperty("parallel_id")
    private String parallelId;

    @JsonProperty("parallel_start_node_id")
    private String parallelStartNodeId;

    @JsonProperty("parent_parallel_id")
    private String parentParallelId;

    @JsonProperty("parent_parallel_start_node_id")
    private String parentParallelStartNodeId;

    @JsonProperty("in_iteration_id")
    private String inIterationId;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public NodeTypeEnum getNodeTypeEnum() {
        return nodeType;
    }

    public void setNodeTypeEnum(NodeTypeEnum nodeType) {
        this.nodeType = nodeType;
    }

    public BaseNodeData getNodeData() {
        return nodeData;
    }

    public void setNodeData(BaseNodeData nodeData) {
        this.nodeData = nodeData;
    }

    public RouteNodeState getRouteNodeState() {
        return routeNodeState;
    }

    public void setRouteNodeState(RouteNodeState routeNodeState) {
        this.routeNodeState = routeNodeState;
    }

    public String getParallelId() {
        return parallelId;
    }

    public void setParallelId(String parallelId) {
        this.parallelId = parallelId;
    }

    public String getParallelStartNodeId() {
        return parallelStartNodeId;
    }

    public void setParallelStartNodeId(String parallelStartNodeId) {
        this.parallelStartNodeId = parallelStartNodeId;
    }

    public String getParentParallelId() {
        return parentParallelId;
    }

    public void setParentParallelId(String parentParallelId) {
        this.parentParallelId = parentParallelId;
    }

    public String getParentParallelStartNodeId() {
        return parentParallelStartNodeId;
    }

    public void setParentParallelStartNodeId(String parentParallelStartNodeId) {
        this.parentParallelStartNodeId = parentParallelStartNodeId;
    }

    public String getInIterationId() {
        return inIterationId;
    }

    public void setInIterationId(String inIterationId) {
        this.inIterationId = inIterationId;
    }
}

class NodeRunStartedEvent extends BaseNodeEvent {

    @JsonProperty("predecessor_node_id")
    private String predecessorNodeId;

    // Getter and setter
    public String getPredecessorNodeId() {
        return predecessorNodeId;
    }

    public void setPredecessorNodeId(String predecessorNodeId) {
        this.predecessorNodeId = predecessorNodeId;
    }
}

class NodeRunStreamChunkEvent extends BaseNodeEvent {

    @JsonProperty("chunk_content")
    private String chunkContent;

    @JsonProperty("from_variable_selector")
    private List<String> fromVariableSelector;

    // Getters and setters
    public String getChunkContent() {
        return chunkContent;
    }

    public void setChunkContent(String chunkContent) {
        this.chunkContent = chunkContent;
    }

    public List<String> getFromVariableSelector() {
        return fromVariableSelector;
    }

    public void setFromVariableSelector(List<String> fromVariableSelector) {
        this.fromVariableSelector = fromVariableSelector;
    }
}

class NodeRunRetrieverResourceEvent extends BaseNodeEvent {

    @JsonProperty("retriever_resources")
    private List<Map<String, Object>> retrieverResources;

    @JsonProperty("context")
    private String context;

    // Getters and setters
    public List<Map<String, Object>> getRetrieverResources() {
        return retrieverResources;
    }

    public void setRetrieverResources(List<Map<String, Object>> retrieverResources) {
        this.retrieverResources = retrieverResources;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}

class NodeRunSucceededEvent extends BaseNodeEvent {
}

class NodeRunFailedEvent extends BaseNodeEvent {

    @JsonProperty("error")
    private String error;

    // Getter and setter
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

class BaseParallelBranchEvent extends GraphEngineEvent {

    @JsonProperty("parallel_id")
    private String parallelId;

    @JsonProperty("parallel_start_node_id")
    private String parallelStartNodeId;

    @JsonProperty("parent_parallel_id")
    private String parentParallelId;

    @JsonProperty("parent_parallel_start_node_id")
    private String parentParallelStartNodeId;

    @JsonProperty("in_iteration_id")
    private String inIterationId;

    // Getters and setters
    public String getParallelId() {
        return parallelId;
    }

    public void setParallelId(String parallelId) {
        this.parallelId = parallelId;
    }

    public String getParallelStartNodeId() {
        return parallelStartNodeId;
    }

    public void setParallelStartNodeId(String parallelStartNodeId) {
        this.parallelStartNodeId = parallelStartNodeId;
    }

    public String getParentParallelId() {
        return parentParallelId;
    }

    public void setParentParallelId(String parentParallelId) {
        this.parentParallelId = parentParallelId;
    }

    public String getParentParallelStartNodeId() {
        return parentParallelStartNodeId;
    }

    public void setParentParallelStartNodeId(String parentParallelStartNodeId) {
        this.parentParallelStartNodeId = parentParallelStartNodeId;
    }

    public String getInIterationId() {
        return inIterationId;
    }

    public void setInIterationId(String inIterationId) {
        this.inIterationId = inIterationId;
    }
}

class ParallelBranchRunStartedEvent extends BaseParallelBranchEvent {
}

class ParallelBranchRunSucceededEvent extends BaseParallelBranchEvent {
}

class ParallelBranchRunFailedEvent extends BaseParallelBranchEvent {

    @JsonProperty("error")
    private String error;

    // Getter and setter
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

class BaseIterationEvent extends GraphEngineEvent {

    @JsonProperty("iteration_id")
    private String iterationId;

    @JsonProperty("iteration_node_id")
    private String iterationNodeId;

    @JsonProperty("iteration_node_type")
    private NodeTypeEnum iterationNodeTypeEnum;

    @JsonProperty("iteration_node_data")
    private BaseNodeData iterationNodeData;

    @JsonProperty("parallel_id")
    private String parallelId;

    @JsonProperty("parallel_start_node_id")
    private String parallelStartNodeId;

    @JsonProperty("parent_parallel_id")
    private String parentParallelId;

    @JsonProperty("parent_parallel_start_node_id")
    private String parentParallelStartNodeId;

    // Getters and setters
    public String getIterationId() {
        return iterationId;
    }

    public void setIterationId(String iterationId) {
        this.iterationId = iterationId;
    }

    public String getIterationNodeId() {
        return iterationNodeId;
    }

    public void setIterationNodeId(String iterationNodeId) {
        this.iterationNodeId = iterationNodeId;
    }

    public NodeTypeEnum getIterationNodeTypeEnum() {
        return iterationNodeTypeEnum;
    }

    public void setIterationNodeTypeEnum(NodeTypeEnum iterationNodeTypeEnum) {
        this.iterationNodeTypeEnum = iterationNodeTypeEnum;
    }

    public BaseNodeData getIterationNodeData() {
        return iterationNodeData;
    }

    public void setIterationNodeData(BaseNodeData iterationNodeData) {
        this.iterationNodeData = iterationNodeData;
    }

    public String getParallelId() {
        return parallelId;
    }

    public void setParallelId(String parallelId) {
        this.parallelId = parallelId;
    }

    public String getParallelStartNodeId() {
        return parallelStartNodeId;
    }

    public void setParallelStartNodeId(String parallelStartNodeId) {
        this.parallelStartNodeId = parallelStartNodeId;
    }

    public String getParentParallelId() {
        return parentParallelId;
    }

    public void setParentParallelId(String parentParallelId) {
        this.parentParallelId = parentParallelId;
    }

    public String getParentParallelStartNodeId() {
        return parentParallelStartNodeId;
    }

    public void setParentParallelStartNodeId(String parentParallelStartNodeId) {
        this.parentParallelStartNodeId = parentParallelStartNodeId;
    }
}

class IterationRunStartedEvent extends BaseIterationEvent {

    @JsonProperty("start_at")
    private Date startAt;

    @JsonProperty("inputs")
    private Map<String, Object> inputs;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    @JsonProperty("predecessor_node_id")
    private String predecessorNodeId;

    // Getters and setters
    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public String getPredecessorNodeId() {
        return predecessorNodeId;
    }

    public void setPredecessorNodeId(String predecessorNodeId) {
        this.predecessorNodeId = predecessorNodeId;
    }
}

class IterationRunNextEvent extends BaseIterationEvent {

    @JsonProperty("index")
    private int index;

    @JsonProperty("pre_iteration_output")
    private Object preIterationOutput;

    // Getters and setters
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Object getPreIterationOutput() {
        return preIterationOutput;
    }

    public void setPreIterationOutput(Object preIterationOutput) {
        this.preIterationOutput = preIterationOutput;
    }
}

class IterationRunSucceededEvent extends BaseIterationEvent {

    @JsonProperty("start_at")
    private Date startAt;

    @JsonProperty("inputs")
    private Map<String, Object> inputs;

    @JsonProperty("outputs")
    private Map<String, Object> outputs;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    @JsonProperty("steps")
    private int steps;

    // Getters and setters
    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, Object> outputs) {
        this.outputs = outputs;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }
}

class IterationRunFailedEvent extends BaseIterationEvent {

    @JsonProperty("start_at")
    private Date startAt;

    @JsonProperty("inputs")
    private Map<String, Object> inputs;

    @JsonProperty("outputs")
    private Map<String, Object> outputs;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    @JsonProperty("steps")
    private int steps;

    @JsonProperty("error")
    private String error;

    // Getters and setters
    public Date getStartAt() {
        return startAt;
    }

    public void setStartAt(Date startAt) {
        this.startAt = startAt;
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public Map<String, Object> getOutputs() {
        return outputs;
    }

    public void setOutputs(Map<String, Object> outputs) {
        this.outputs = outputs;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}

interface InNodeEvent {
}