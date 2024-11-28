package com.hwq.dataloom.core.workflow.queue.event;

import lombok.Data;

//@Data
public class QueueLLMChunkEvent extends BaseQueueEvent {
//    private LLMResultChunk chunk;

    public QueueLLMChunkEvent() {
        super(QueueEventEnum.LLM_CHUNK);
    }
}