package com.hwq.dataloom.manager.message.update_data;

import com.hwq.dataloom.manager.message.ISendMessage;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/15
 * @Description:
 **/
public interface IUpdateDataMessage extends ISendMessage {

    String buildMessage(String userName,String updateContent);
}
