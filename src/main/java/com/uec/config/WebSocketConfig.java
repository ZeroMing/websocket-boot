package com.uec.config;

import com.uec.websocket.HandshakeInterceptor;
import com.uec.websocket.SystemWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @description: WebSocket配置信息
 * @author: Ming.Lee/李明
 * @create: 2018-12-10 13:59
 **/
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        //html5的方式建立连接
        registry.addHandler(SystemWebSocketHandler(), "/websocket/h5")
                .addInterceptors(httpSessionHandshakeInterceptor())
                .setAllowedOrigins("*"); //可指定多个跨域，如果无需限制可使用 *;


        //sockjs的方式建立连接
        registry.addHandler(SystemWebSocketHandler(), "/websocket/sockjs")
                .addInterceptors(httpSessionHandshakeInterceptor())
                .setAllowedOrigins("*") //可指定多个跨域，如果无需限制可使用 *;
                .withSockJS();
    }



    //用于定义 WebSocket 的消息处理
    @Bean
    public WebSocketHandler SystemWebSocketHandler() {
        return new SystemWebSocketHandler();
    }


    //用于处理 WebSocket 连接
    @Bean
    public HandshakeInterceptor httpSessionHandshakeInterceptor(){
        return new HandshakeInterceptor();
    }


}