package com.hwq.dataloom.core.workflow.enums;

import lombok.Getter;

/**
 * 发布来源枚举类
 */
@Getter
public enum PublishFrom {
    TASK_PIPELINE(1),
    APPLICATION_MANAGER(2);

    private final int value;

    PublishFrom(int value) {
        this.value = value;
    }

}