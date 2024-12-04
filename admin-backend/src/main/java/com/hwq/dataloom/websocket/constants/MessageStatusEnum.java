package com.hwq.dataloom.websocket.constants;

/**
 * @author HWQ
 * @date 2024/11/1 22:58
 * @description 程序运行状态枚举类
 */
public enum MessageStatusEnum {

    START("start", 0),
    ANALYSIS_COMPLETE("analysis_complete", 1),
    ANALYSIS_RELATE_TABLE_COMPLETE("analysis_relate_table_complete", 2),
    ALL_COMPLETE("all_complete", 3),
    END("end", 4),
    ERROR("error", 5);

    private final String status;

    private final int value;

    MessageStatusEnum(String status, int value) {
        this.status = status;
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public int getValue() {
        return value;
    }
}
