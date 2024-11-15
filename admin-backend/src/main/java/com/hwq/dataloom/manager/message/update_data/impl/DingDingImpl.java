package com.hwq.dataloom.manager.message.update_data.impl;

import com.hwq.dataloom.manager.message.update_data.IUpdateDataMessage;
import org.springframework.stereotype.Component;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/11
 * @Description:
 **/
@Component("update_date_dingding")
public class DingDingImpl implements IUpdateDataMessage {
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
