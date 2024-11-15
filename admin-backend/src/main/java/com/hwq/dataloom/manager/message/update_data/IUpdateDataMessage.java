package com.hwq.dataloom.manager.message.update_data;

import com.hwq.dataloom.manager.message.ISendMessage;
import com.hwq.dataloom.manager.model.UpdateDataBuildMessage;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/15
 * @Description:
 **/
public interface IUpdateDataMessage extends ISendMessage<UpdateDataBuildMessage> {

    String buildMessage(UpdateDataBuildMessage buildMessage);
}
