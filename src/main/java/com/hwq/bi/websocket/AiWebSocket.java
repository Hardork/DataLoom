package com.hwq.bi.websocket;

/**
 * @Author:HWQ
 * @DateTime:2023/9/24 14:16
 * @Description:
 **/

import cn.hutool.json.JSONUtil;
import com.hwq.bi.websocket.vo.AiWebSocketVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@Slf4j
@ServerEndpoint("/websocket/ai/{userId}")  // 接口路径 ws://localhost:8081/webSocket/userId;
public class AiWebSocket {

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    /**
     * 用户ID
     */
    private Long userId;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    //  注：底下WebSocket是当前类名
    private static CopyOnWriteArraySet<AiWebSocket> webSockets =new CopyOnWriteArraySet<>();
    // 用来存在线连接用户信息
    private static ConcurrentHashMap<Long,Session> sessionPool = new ConcurrentHashMap<Long,Session>();

    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value="userId")Long userId) {
        try {
            this.session = session;
            this.userId = userId;
            webSockets.add(this);
            sessionPool.put(userId, session);
            log.info("【websocket消息】有新的连接，总数为:"+webSockets.size());
        } catch (Exception e) {
        }
    }

    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        try {
            webSockets.remove(this);
            sessionPool.remove(this.userId);
            log.info("【websocket消息】连接断开，总数为:"+webSockets.size());
        } catch (Exception e) {
        }
    }
    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("【websocket消息】收到客户端消息:"+message);
    }

    /** 发送错误时的处理
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {

        log.error("用户错误,原因:"+error.getMessage());
        error.printStackTrace();
    }


    // 此为单点消息
    public void sendOneMessage(long userId, AiWebSocketVO aiWebSocketVO) {
        Session session = sessionPool.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String res = JSONUtil.toJsonStr(aiWebSocketVO);
                log.info("【websocket消息】 单点消息:" + res);
                session.getAsyncRemote().sendText(res);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.error("不存在该用户");
        }
    }

}