package com.hwq.dataloom.manager.message;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/11
 * @Description:
 **/
public interface ISendMessage<T> {

    String mark();

    void send(String message,String account);

    String buildMessage(T buildMessage);
}
