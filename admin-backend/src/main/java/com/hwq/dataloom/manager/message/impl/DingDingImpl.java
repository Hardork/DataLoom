package com.hwq.dataloom.manager.message.impl;

import com.hwq.dataloom.manager.message.ISendMessage;
import org.springframework.stereotype.Component;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/11
 * @Description:
 **/
@Component("dingding")
public class DingDingImpl implements ISendMessage {
    @Override
    public String mark() {
        return "dingding";
    }

    @Override
    public void send(String message,String account) {

    }

    @Override
    public String buildMessage(String userName, String updateContent) {
        return null;
    }
}
