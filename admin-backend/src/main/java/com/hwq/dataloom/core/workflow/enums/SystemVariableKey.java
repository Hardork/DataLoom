package com.hwq.dataloom.core.workflow.enums;

/**
 * @author HWQ
 * @date 2024/11/21 23:45
 * @description 系统变量Key枚举
 */
public enum SystemVariableKey {
    QUERY("query"),
    FILES("files"),
    CONVERSATION_ID("conversation_id"),
    USER_ID("user_id"),
    DIALOGUE_COUNT("dialogue_count"),
    APP_ID("app_id"),
    WORKFLOW_ID("workflow_id"),
    WORKFLOW_RUN_ID("workflow_run_id");

    private final String value;

    SystemVariableKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}