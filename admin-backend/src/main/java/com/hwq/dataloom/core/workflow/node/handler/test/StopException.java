package com.hwq.dataloom.core.workflow.node.handler.test;

public final class StopException extends RuntimeException {
    public static final StopException INSTANCE = new StopException();

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }


}