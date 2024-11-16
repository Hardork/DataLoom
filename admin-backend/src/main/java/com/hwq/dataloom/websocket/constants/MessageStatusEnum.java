package com.hwq.dataloom.websocket.constants;

/**
 * @author HWQ
 * @date 2024/11/1 22:58
 * @description
 */
public enum MessageStatusEnum {

    START("start"),
    RUNNING("running"),
    ERROR("error"),
    END("end");

    private final String status;

    MessageStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
