package com.hwq.dataloom.framework.service;

import com.hwq.dataloom.framework.ws.vo.WebSocketMsgVO;

/**
 * @author HWQ
 * @date 2024/8/13 15:05
 * @description websocket RPC调用接口
 */
public interface InnerWSServiceInterface {

    void sendOneMessage(long userId, WebSocketMsgVO webSocketMsgVO);

}