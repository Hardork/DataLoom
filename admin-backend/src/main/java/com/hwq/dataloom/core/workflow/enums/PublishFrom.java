package com.hwq.dataloom.core.workflow.enums;

import lombok.Getter;

/**
 * 消息推送来源枚举
 */
@Getter
enum PublishFrom {
    TASK_PIPELINE(1),
    APPLICATION_MANAGER(2);

    private final int value;

    PublishFrom(int value) {
        this.value = value;
    }

}