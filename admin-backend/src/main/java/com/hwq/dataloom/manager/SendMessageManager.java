package com.hwq.dataloom.manager;

import com.hwq.dataloom.manager.message.ISendMessage;
import com.hwq.dataloom.manager.message.update_data.IUpdateDataMessage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/11
 * @Description: 发送消息管理类 【团队成员修改后发送   todo 进一步通用化】
 **/
@Component
@Deprecated
public class SendMessageManager {

    private Map<String, IUpdateDataMessage> sendMessageMap;

    /**
     * 发送给一个用户
     * @param type
     * @param userName
     * @param updateContent
     * @param account
     * @return
     */
    public boolean send(String type,String userName,String updateContent,String account) {
        IUpdateDataMessage sendMessage = sendMessageMap.get(type);
        String message = sendMessage.buildMessage(userName, updateContent);
        sendMessage.send(message,account);
        return true;
    }


    /**
     * 发送给多个用户
     * @param type
     * @param userName
     * @param updateContent
     * @param accountList
     * @return
     */
    public boolean send(String type, String userName, String updateContent, List<String> accountList) {
        IUpdateDataMessage sendMessage = sendMessageMap.get(type);
        String message = sendMessage.buildMessage(userName, updateContent);
        for (String account : accountList) {
            sendMessage.send(message,account);
        }
        return true;
    }
}
