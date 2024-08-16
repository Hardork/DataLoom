package com.hwq.dataloom.service.impl.inner;

import com.hwq.dataloom.framework.service.InnerWSServiceInterface;
import com.hwq.dataloom.framework.ws.vo.WebSocketMsgVO;
import com.hwq.dataloom.websocket.UserWebSocket;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @author HWQ
 * @date 2024/8/13 15:11
 * @description
 */
@DubboService
public class InnerWSServiceInterfaceImpl implements InnerWSServiceInterface {

    @Resource
    private UserWebSocket userWebSocket;
    @Override
    public void sendOneMessage(long userId, WebSocketMsgVO webSocketMsgVO) {
        userWebSocket.sendOneMessage(userId, webSocketMsgVO);
    }
}
