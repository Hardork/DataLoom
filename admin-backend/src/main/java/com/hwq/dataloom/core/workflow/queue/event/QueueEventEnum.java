package com.hwq.dataloom.core.workflow.queue.event;

/**
 * @author HWQ
 * @date 2024/11/28 19:44
 * @description 队列事件枚举
 */
public enum QueueEventEnum {
    LLM_CHUNK("llm_chunk"),
    TEXT_CHUNK("text_chunk"),
    AGENT_MESSAGE("agent_message"),
    MESSAGE_REPLACE("message_replace"),
    MESSAGE_END("message_end"),
    ADVANCED_CHAT_MESSAGE_END("advanced_chat_message_end"),
    WORKFLOW_STARTED("workflow_started"),
    WORKFLOW_SUCCEEDED("workflow_succeeded"),
    WORKFLOW_FAILED("workflow_failed"),
    ITERATION_START("iteration_start"),
    ITERATION_NEXT("iteration_next"),
    ITERATION_COMPLETED("iteration_completed"),
    NODE_STARTED("node_started"),
    NODE_SUCCEEDED("node_succeeded"),
    NODE_FAILED("node_failed"),
    RETRIEVER_RESOURCES("retriever_resources"),
    ANNOTATION_REPLY("annotation_reply"),
    AGENT_THOUGHT("agent_thought"),
    MESSAGE_FILE("message_file"),
    PARALLEL_BRANCH_RUN_STARTED("parallel_branch_run_started"),
    PARALLEL_BRANCH_RUN_SUCCEEDED("parallel_branch_run_succeeded"),
    PARALLEL_BRANCH_RUN_FAILED("parallel_branch_run_failed"),
    ERROR("error"),
    PING("ping"),
    STOP("stop");

    private final String value;

    QueueEventEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}