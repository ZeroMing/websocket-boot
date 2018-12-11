package com.uec.websocket;
import com.uec.domain.SessionUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import java.util.Map;

@Slf4j
@Component
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    /**
     * 握手之前
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("Before Handshake<<<<<");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            String token = servletRequest.getServletRequest().getParameter("token");
            String username = "";
            //必须提供 token 请求参数，否则不允许连接
            if (StringUtils.isEmpty(token))
                return false;
            username = token.split("_")[0];
            //真实场景中可以调用数据库或其他信息
            SessionUser sessionUser = new SessionUser();
            sessionUser.setUserName(username);
            attributes.put(SystemWebSocketHandler.WEB_SOCKET_USERNAME, sessionUser);
        }

        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    //握手后
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        log.info("After Handshake>>>>>");
        super.afterHandshake(request, response, wsHandler, ex);
    }

}