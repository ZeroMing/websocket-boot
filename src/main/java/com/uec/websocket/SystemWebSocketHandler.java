package com.uec.websocket;

import com.uec.domain.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
public class SystemWebSocketHandler implements WebSocketHandler {

    public static final String WEB_SOCKET_USERNAME = "webSocketUsername";

    //所有连接
    private static CopyOnWriteArraySet<WebSocketSession> webSocketSet = new CopyOnWriteArraySet<WebSocketSession>();

    //各用户的连接
    private static Map<String,List<WebSocketSession>> webSocketMap = new HashMap<String,List<WebSocketSession>>();

    //每个页面的连接（按httpSession分）
    private static Map<String, WebSocketSession> wsSessionMap = new HashMap<String, WebSocketSession>();


    public static CopyOnWriteArraySet<WebSocketSession> getWebSocketSet() {
        return webSocketSet;
    }

    public static Map<String, List<WebSocketSession>> getWebSocketMap() {
        return webSocketMap;
    }

    public static Map<String, WebSocketSession> getWsSessionMap() {
        return wsSessionMap;
    }

    //页面操作提示（只向操作页面发送）
    public static void sendOperateMsg(String httpSessionId,String msg) throws IOException {
        WebSocketSession wssession = wsSessionMap.get(httpSessionId);
        wssession.sendMessage(new TextMessage(msg));
    }


    //向用户发送websocket消息
    public static void sendMsgToUser(String username, String msg) throws IOException{
        List<WebSocketSession> sessionList = webSocketMap.get(username);
        if(!CollectionUtils.isEmpty(sessionList)){
            for (WebSocketSession webSocketSession : sessionList) {
                webSocketSession.sendMessage(new TextMessage(msg));
            }
        }
    }



    //向所有人发送websocket消息
    public static void sendMsgToAll( String msg) throws IOException{
        if(!CollectionUtils.isEmpty(webSocketSet)){
            for (WebSocketSession webSocketSession : webSocketSet) {
                webSocketSession.sendMessage(new TextMessage(msg));
            }
        }

    }

    /**
     * 连接建立后处理
     * @param wssession
     * @throws IOException
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession wssession) throws IOException {
//        wssession.sendMessage(new TextMessage("Server:connected OK!"));
        Map<String, Object> map = wssession.getAttributes();
        //获取session中的用户实体
        SessionUser user = (SessionUser)wssession.getAttributes().get(WEB_SOCKET_USERNAME);
        log.info("连接建立...");
        if(null != user){
            List<WebSocketSession> sessionList = webSocketMap.get(user.getUserName());
            if(!CollectionUtils.isEmpty(sessionList)){
                sessionList.add(wssession);
            }else{
                sessionList = new ArrayList<>();
                sessionList.add(wssession);
                webSocketMap.put(user.getUserName(), sessionList);
            }
        }

        webSocketSet.add(wssession);
        //获取httpSessionId
        String sessionId = (String)map.get("HTTP.SESSION.ID");
        wsSessionMap.put(sessionId, wssession);

    }

    /**
     * 连接关闭后处理
     * @param wss
     * @param cs
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession wss, CloseStatus cs) throws Exception {
        SessionUser user = (SessionUser)wss.getAttributes().get(WEB_SOCKET_USERNAME);
        if(null != user){
            List<WebSocketSession> sessionList = webSocketMap.get(user.getUserName());
            if(!CollectionUtils.isEmpty(sessionList)){
                sessionList.remove(wss);
            }else{
                webSocketMap.remove(user.getUserName());
            }
        }
        webSocketSet.remove(wss);
        String sessionId = (String)wss.getAttributes().get("HTTP.SESSION.ID");
        wsSessionMap.remove(sessionId);
    }

    @Override
    public void handleMessage(WebSocketSession wss, WebSocketMessage<?> wsm) throws Exception {
        TextMessage returnMessage = new TextMessage(wsm.getPayload() + " received at server");
        wss.sendMessage(returnMessage);
    }

    /**
     * 抛出异常时处理
     * @param wss
     * @param thrwbl
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession wss, Throwable thrwbl) throws Exception {
        if(wss.isOpen()){
            wss.close();
        }
    }


    /**
     * 是否支持局部消息
     * @return
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}

