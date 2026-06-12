package com.example.zaran_design_backend.config;

import com.example.zaran_design_backend.security.JwtUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置，注册协同编辑的 WebSocket 端点。
 * 端点路径：/ws/collab/{sessionId}?token=xxx
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CollabWebSocketHandler collabWebSocketHandler;
    private final JwtUtils jwtUtils;

    public WebSocketConfig(CollabWebSocketHandler collabWebSocketHandler, JwtUtils jwtUtils) {
        this.collabWebSocketHandler = collabWebSocketHandler;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(collabWebSocketHandler, "/ws/collab/{sessionId}")
                .setAllowedOrigins("*");
    }
}
