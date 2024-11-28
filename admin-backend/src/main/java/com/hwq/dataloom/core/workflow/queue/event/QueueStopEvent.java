package com.hwq.dataloom.core.workflow.queue.event;

/**
 * @author HWQ
 * @date 2024/11/28 23:42
 * @description
 */
public class QueueStopEvent extends BaseQueueEvent{

    private StopBy stopBy;
    public enum StopBy {
        // 流程因用户手动操作而停止
        USER_MANUAL("user-manual"),
        // 流程因注释回复相关情况而停止
        ANNOTATION_REPLY("annotation-reply"),
        OUTPUT_MODERATION("output-moderation"),
        INPUT_MODERATION("input-moderation")
        ;

        private final String value;

        StopBy(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public QueueStopEvent(StopBy stopBy) {
        super(QueueEventEnum.STOP);
        this.stopBy = stopBy;
    }
}
