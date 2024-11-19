package com.hwq.dataloom.manager.message.update_data.impl;

import com.hwq.dataloom.manager.message.update_data.IUpdateDataMessageService;
import com.hwq.dataloom.manager.model.UpdateDataBuildMessage;
import org.springframework.stereotype.Component;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/11
 * @Description:
 **/
@Component("update_date_dingding")
public class DingDingImpl implements IUpdateDataMessageService {
    @Override
    public String mark() {
        return "dingding";
    }

    @Override
    public void send(String message,String account) {

    }

    @Override
    public String buildMessage(UpdateDataBuildMessage buildMessage) {
        return null;
    }
}
