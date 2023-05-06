package com.xiaojiang.fmmall.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author xiaojiang
 * @Date 2023-04-14 21:31
 * @Description
 **/
@Component
@ServerEndpoint("/webSocket/{oid}")
public class WebSocketServer {
    private static ConcurrentHashMap<String,Session> sessionMap= new ConcurrentHashMap<>();

    /**
     *前端发送请求建立websocket连接,就会执行@OnOpen方法
     */
    @OnOpen
    public void open(@PathParam("oid") String orderId, Session session){
        System.out.println("---------------"+orderId);
        sessionMap.put(orderId,session);
    }

    /**
     * 前端关闭页面或者主动关闭websocket连接,就会执行@Onclose方法
     */
    @OnClose
    public void close(@PathParam("oid") String orderId){
        sessionMap.remove(orderId);
    }

    //发送消息
    public static void sendMsg(String orderId,String msg){
        Session session = sessionMap.get(orderId);
        try {
            session.getBasicRemote().sendText(msg);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
