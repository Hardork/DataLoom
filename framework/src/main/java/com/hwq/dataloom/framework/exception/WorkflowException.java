package com.hwq.dataloom.framework.exception;

import com.hwq.dataloom.framework.errorcode.ErrorCode;

/**
 * @Author: HWQ
 * @Description: 工作流异常
 * @DateTime: 2024/12/11 9:05
 **/
public class WorkflowException extends RuntimeException {
    /**
     * 错误码
     */
    private final int code;

    public WorkflowException(String message, int code) {
        super(message);
        this.code = code;
    }


    public WorkflowException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public WorkflowException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
