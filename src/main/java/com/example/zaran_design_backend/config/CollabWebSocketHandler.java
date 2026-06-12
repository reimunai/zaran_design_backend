package com.example.zaran_design_backend.config;

import com.example.zaran_design_backend.repository.CollabParticipantRepository;
import com.example.zaran_design_backend.repository.CollabSessionRepository;
import com.example.zaran_design_backend.repository.UserRepository;
import com.example.zaran_design_backend.security.JwtUtils;
import com.example.zaran_design_backend.service.CollabService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 协同编辑 WebSocket 处理器。
 *
 * <p>处理客户端与服务端之间的实时双向通信，支持以下消息类型：</p>
 * <ul>
 *   <li><b>客户端 → 服务端</b>：CURSOR_MOVE、DRAW、LAYER_CHANGE、ANNOTATION、CHAT、PING</li>
 *   <li><b>服务端 → 客户端（广播）</b>：REMOTE_OPERATION、USER_JOIN、USER_LEAVE、VERSION_SAVED、PONG、ERROR</li>
 * </ul>
 *
 * <p>连接认证通过 URL 参数 ?token=xxx 传递 JWT。</p>
 */
@Component
public class CollabWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(CollabWebSocketHandler.class);

    private final JwtUtils jwtUtils;
    private final CollabService collabService;
    private final CollabSessionRepository sessionRepository;
    private final CollabParticipantRepository participantRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    /**
     * sessionId -> 该协同会话中所有 WebSocket 连接的集合
     */
    private final Map<Integer, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    /**
     * WebSocketSession.id -> 绑定的 userId
     */
    private final Map<String, Integer> sessionUserMap = new ConcurrentHashMap<>();

    /**
     * WebSocketSession.id -> 绑定的 sessionId（协同会话）
     */
    private final Map<String, Integer> sessionRoomMap = new ConcurrentHashMap<>();

    public CollabWebSocketHandler(JwtUtils jwtUtils,
                                  CollabService collabService,
                                  CollabSessionRepository sessionRepository,
                                  CollabParticipantRepository participantRepository,
                                  UserRepository userRepository,
                                  ObjectMapper objectMapper) {
        this.jwtUtils = jwtUtils;
        this.collabService = collabService;
        this.sessionRepository = sessionRepository;
        this.participantRepository = participantRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession wsSession) throws Exception {
        // 1. 从 URL 中提取 sessionId
        Integer collabSessionId = extractSessionId(wsSession);
        if (collabSessionId == null) {
            wsSession.close(CloseStatus.BAD_DATA);
            return;
        }

        // 2. 验证 JWT Token
        String token = extractToken(wsSession);
        if (token == null || !jwtUtils.validateToken(token)) {
            sendError(wsSession, 401, "认证失败，Token 无效或已过期");
            wsSession.close(new CloseStatus(4401, "Unauthorized"));
            return;
        }

        Integer userId = jwtUtils.getUserIdFromToken(token);

        // 3. 校验协同会话是否存在
        if (sessionRepository.findById(collabSessionId).isEmpty()) {
            sendError(wsSession, 404, "协同会话不存在");
            wsSession.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        // 4. 校验用户是否为该会话的参与者
        if (!participantRepository.existsBySessionIdAndUserId(collabSessionId, userId)) {
            sendError(wsSession, 403, "您不是该会话的参与者");
            wsSession.close(new CloseStatus(4403, "Forbidden"));
            return;
        }

        // 5. 注册到会话房间
        rooms.computeIfAbsent(collabSessionId, k -> new CopyOnWriteArraySet<>()).add(wsSession);
        sessionUserMap.put(wsSession.getId(), userId);
        sessionRoomMap.put(wsSession.getId(), collabSessionId);

        log.info("用户 {} 加入协同会话 {}", userId, collabSessionId);

        // 6. 广播 USER_JOIN 给房间内其他用户
        String username = userRepository.findById(userId)
                .map(u -> u.getUsername())
                .orElse("未知用户");
        String role = userRepository.findById(userId)
                .map(u -> u.getRole().name())
                .orElse("tourist");

        ObjectNode joinMsg = objectMapper.createObjectNode();
        joinMsg.put("type", "USER_JOIN");
        ObjectNode userNode = joinMsg.putObject("user");
        userNode.put("userId", userId);
        userNode.put("username", username);
        userNode.put("role", role);
        userNode.put("avatar", "");

        List<Integer> onlineUsers = getOnlineUserIds(collabSessionId);
        joinMsg.putPOJO("onlineUsers", onlineUsers);

        broadcastToRoom(collabSessionId, joinMsg.toString(), wsSession);
    }

    @Override
    protected void handleTextMessage(WebSocketSession wsSession, TextMessage message) throws Exception {
        Integer userId = sessionUserMap.get(wsSession.getId());
        Integer collabSessionId = sessionRoomMap.get(wsSession.getId());

        if (userId == null || collabSessionId == null) {
            sendError(wsSession, 401, "未认证的连接");
            return;
        }

        JsonNode msg;
        try {
            msg = objectMapper.readTree(message.getPayload());
        } catch (Exception e) {
            sendError(wsSession, 400, "消息格式错误");
            return;
        }

        String type = msg.has("type") ? msg.get("type").asText() : "";
        if (type.isBlank()) {
            sendError(wsSession, 400, "缺少 type 字段");
            return;
        }

        switch (type) {
            case "PING" -> handlePing(wsSession);
            case "CURSOR_MOVE" -> handleCursorMove(collabSessionId, userId, msg, wsSession);
            case "DRAW" -> handleDraw(collabSessionId, userId, msg, wsSession);
            case "LAYER_CHANGE" -> handleLayerChange(collabSessionId, userId, msg, wsSession);
            case "ANNOTATION" -> handleAnnotation(collabSessionId, userId, msg, wsSession);
            case "CHAT" -> handleChat(collabSessionId, userId, msg, wsSession);
            default -> sendError(wsSession, 400, "不支持的消息类型: " + type);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession wsSession, CloseStatus status) {
        Integer userId = sessionUserMap.remove(wsSession.getId());
        Integer collabSessionId = sessionRoomMap.remove(wsSession.getId());

        if (collabSessionId != null) {
            Set<WebSocketSession> room = rooms.get(collabSessionId);
            if (room != null) {
                room.remove(wsSession);
                if (room.isEmpty()) {
                    rooms.remove(collabSessionId);
                }
            }

            // 广播 USER_LEAVE
            if (userId != null) {
                try {
                    ObjectNode leaveMsg = objectMapper.createObjectNode();
                    leaveMsg.put("type", "USER_LEAVE");
                    leaveMsg.put("userId", userId);
                    List<Integer> onlineUsers = getOnlineUserIds(collabSessionId);
                    leaveMsg.putPOJO("onlineUsers", onlineUsers);
                    broadcastToRoom(collabSessionId, leaveMsg.toString(), null);
                } catch (Exception e) {
                    log.error("广播 USER_LEAVE 失败", e);
                }
                log.info("用户 {} 离开协同会话 {}", userId, collabSessionId);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket 传输错误: session={}", session.getId(), exception);
    }

    // ============================ 消息处理 ============================

    private void handlePing(WebSocketSession wsSession) throws IOException {
        ObjectNode pong = objectMapper.createObjectNode();
        pong.put("type", "PONG");
        pong.put("timestamp", System.currentTimeMillis());
        wsSession.sendMessage(new TextMessage(pong.toString()));
    }

    private void handleCursorMove(Integer sessionId, Integer userId, JsonNode msg, WebSocketSession wsSession) {
        broadcastRemoteOperation(sessionId, userId, "CURSOR_MOVE", msg, wsSession);
    }

    private void handleDraw(Integer sessionId, Integer userId, JsonNode msg, WebSocketSession wsSession) {
        // 记录操作到数据库
        JsonNode opData = extractOpData(msg);
        collabService.recordOperation(sessionId, userId, "draw", opData);
        broadcastRemoteOperation(sessionId, userId, "DRAW", msg, wsSession);
    }

    private void handleLayerChange(Integer sessionId, Integer userId, JsonNode msg, WebSocketSession wsSession) {
        String action = msg.has("action") ? msg.get("action").asText() : "update";
        JsonNode opData = extractOpData(msg);
        collabService.recordOperation(sessionId, userId, "layer_" + action, opData);
        broadcastRemoteOperation(sessionId, userId, "LAYER_CHANGE", msg, wsSession);
    }

    private void handleAnnotation(Integer sessionId, Integer userId, JsonNode msg, WebSocketSession wsSession) {
        collabService.recordOperation(sessionId, userId, "annotation", extractOpData(msg));
        broadcastRemoteOperation(sessionId, userId, "ANNOTATION", msg, wsSession);
    }

    private void handleChat(Integer sessionId, Integer userId, JsonNode msg, WebSocketSession wsSession) {
        collabService.recordOperation(sessionId, userId, "chat", extractOpData(msg));
        broadcastRemoteOperation(sessionId, userId, "CHAT", msg, wsSession);
    }

    // ============================ 广播辅助 ============================

    /**
     * 向房间内其他用户广播远程操作。
     */
    private void broadcastRemoteOperation(Integer sessionId, Integer userId, String opType,
                                          JsonNode originalMsg, WebSocketSession excludeSession) {
        try {
            String username = userRepository.findById(userId)
                    .map(u -> u.getUsername())
                    .orElse("未知用户");
            String avatar = userRepository.findById(userId)
                    .map(u -> u.getAvatar())
                    .orElse("");

            ObjectNode broadcast = objectMapper.createObjectNode();
            broadcast.put("type", "REMOTE_OPERATION");
            broadcast.put("userId", userId);
            broadcast.put("username", username);
            broadcast.put("avatar", avatar != null ? avatar : "");

            // 构建操作对象
            ObjectNode operation = broadcast.putObject("operation");
            operation.put("type", opType);
            // 复制原始消息中的关键字段
            if (originalMsg.has("layerId")) {
                operation.set("layerId", originalMsg.get("layerId"));
            }
            if (originalMsg.has("path")) {
                operation.set("path", originalMsg.get("path"));
            }
            if (originalMsg.has("action")) {
                operation.put("action", originalMsg.get("action").asText());
            }
            if (originalMsg.has("data")) {
                operation.set("data", originalMsg.get("data"));
            }
            if (originalMsg.has("x")) {
                operation.put("x", originalMsg.get("x").asInt());
            }
            if (originalMsg.has("y")) {
                operation.put("y", originalMsg.get("y").asInt());
            }
            if (originalMsg.has("content")) {
                operation.put("content", originalMsg.get("content").asText());
            }
            if (originalMsg.has("mentionUserId")) {
                operation.set("mentionUserId", originalMsg.get("mentionUserId"));
            }
            if (originalMsg.has("mentionUserIds")) {
                operation.set("mentionUserIds", originalMsg.get("mentionUserIds"));
            }
            if (originalMsg.has("tool")) {
                operation.put("tool", originalMsg.get("tool").asText());
            }

            broadcast.put("timestamp", System.currentTimeMillis());

            broadcastToRoom(sessionId, broadcast.toString(), excludeSession);
        } catch (Exception e) {
            log.error("广播远程操作失败", e);
        }
    }

    /**
     * 向房间内所有连接广播消息（可排除某个连接）。
     */
    private void broadcastToRoom(Integer sessionId, String message, WebSocketSession exclude) {
        Set<WebSocketSession> room = rooms.get(sessionId);
        if (room == null || room.isEmpty()) {
            return;
        }

        TextMessage textMessage = new TextMessage(message);
        for (WebSocketSession ws : room) {
            if (ws.isOpen() && (exclude == null || !ws.getId().equals(exclude.getId()))) {
                try {
                    ws.sendMessage(textMessage);
                } catch (IOException e) {
                    log.error("发送消息失败: session={}", ws.getId(), e);
                }
            }
        }
    }

    // ============================ 辅助方法 ============================

    private void sendError(WebSocketSession wsSession, int errorCode, String errorMessage) {
        try {
            ObjectNode error = objectMapper.createObjectNode();
            error.put("type", "ERROR");
            error.put("errorCode", errorCode);
            error.put("errorMessage", errorMessage);
            wsSession.sendMessage(new TextMessage(error.toString()));
        } catch (IOException e) {
            log.error("发送错误消息失败", e);
        }
    }

    private Integer extractSessionId(WebSocketSession wsSession) {
        URI uri = wsSession.getUri();
        if (uri == null) return null;
        String path = uri.getPath();
        // 路径格式: /ws/collab/{sessionId}
        String[] parts = path.split("/");
        if (parts.length >= 4) {
            try {
                return Integer.parseInt(parts[parts.length - 1]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String extractToken(WebSocketSession wsSession) {
        URI uri = wsSession.getUri();
        if (uri == null) return null;
        String query = uri.getQuery();
        if (query == null) return null;
        for (String param : query.split("&")) {
            String[] kv = param.split("=", 2);
            if (kv.length == 2 && "token".equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }

    /** 提取操作数据（排除 type 和 timestamp 等元数据） */
    private JsonNode extractOpData(JsonNode msg) {
        return msg;
    }

    /** 获取某协同会话的在线用户ID列表 */
    private List<Integer> getOnlineUserIds(Integer sessionId) {
        Set<WebSocketSession> room = rooms.get(sessionId);
        if (room == null) return List.of();
        return room.stream()
                .map(ws -> sessionUserMap.get(ws.getId()))
                .filter(id -> id != null)
                .distinct()
                .toList();
    }
}
