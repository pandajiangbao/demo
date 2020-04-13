package com.panda.demo.endPoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Panda
 * @date 10/15/2019
 */
@ServerEndpoint("/endPoint/{userName}") //WebSocket客户端建立连接的地址
@Component
@Slf4j
public class DemoEndPoint {
    private static Map<String, Session> sessions = new ConcurrentHashMap<>();

    public void sendMessageToAll(String message) {
        sessions.forEach((userName, session) -> {
            session.getAsyncRemote().sendText(message);
        });
    }

    public void sendMessageToOne(String userName,String message) {
        sessions.get(userName).getAsyncRemote().sendText(message);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("userName")String userName){
        sessions.put(userName,session);
        sendMessageToAll("用户:"+userName+",连接成功");
        log.info("用户:{},连接成功",userName);
    }

    @OnMessage
    public void onMessage(String message,Session session, @PathParam("userName") String userName) {
        log.info("用户:{},发送信息:{}",userName,message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    @OnClose
    public void onClose(Session session, @PathParam("userName") String userName) {
        sessions.remove(userName);
        sendMessageToAll(userName + "断开连接");
    }
}
