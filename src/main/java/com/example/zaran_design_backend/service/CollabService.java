package com.example.zaran_design_backend.service;

import com.example.zaran_design_backend.dto.*;
import com.example.zaran_design_backend.entity.*;
import com.example.zaran_design_backend.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 协同编辑模块（第7模块）业务逻辑。
 *
 * <p>说明：</p>
 * <ul>
 *   <li>协同编辑会话（collab_sessions）由所有者创建，邀请其他用户加入。</li>
 *   <li>参与者权限分为 view（查看）、comment（批注）、edit（编辑）三级。</li>
 *   <li>操作历史记录每次 WebSocket 推送的编辑操作，按版本号递增。</li>
 *   <li>版本快照在手动调用保存版本接口时生成，支持回滚。</li>
 *   <li>WebSocket 实时通信通过 CollabWebSocketHandler 处理。</li>
 * </ul>
 */
@Service
public class CollabService {

    private final CollabSessionRepository sessionRepository;
    private final CollabParticipantRepository participantRepository;
    private final CollabOperationRepository operationRepository;
    private final CollabSessionVersionRepository versionRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public CollabService(CollabSessionRepository sessionRepository,
                        CollabParticipantRepository participantRepository,
                        CollabOperationRepository operationRepository,
                        CollabSessionVersionRepository versionRepository,
                        UserRepository userRepository,
                        ObjectMapper objectMapper) {
        this.sessionRepository = sessionRepository;
        this.participantRepository = participantRepository;
        this.operationRepository = operationRepository;
        this.versionRepository = versionRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    // ============================ 7.2.1 创建协同会话 ============================

    @Transactional
    public SessionResponse createSession(Integer userId, CreateSessionRequest request) {
        CollabSession session = new CollabSession();
        session.setOwnerId(userId);
        session.setSessionName(request.getSessionName());
        if (request.getPatternId() != null) {
            session.setPatternId(request.getPatternId().intValue());
        }
        session.setStatus(CollabSession.SessionStatus.active);
        session.setCurrentVersion(0);

        sessionRepository.save(session);

        // 所有者自动成为参与者，拥有 edit 权限
        CollabParticipant ownerParticipant = new CollabParticipant();
        ownerParticipant.setSessionId(session.getSessionId());
        ownerParticipant.setUserId(userId);
        ownerParticipant.setPermission(CollabParticipant.CollabPermission.edit);
        participantRepository.save(ownerParticipant);

        // 处理邀请列表
        if (request.getInvitees() != null && !request.getInvitees().isEmpty()) {
            for (CreateSessionRequest.Invitee invitee : request.getInvitees()) {
                if (invitee.getUserId() != null && !invitee.getUserId().equals(userId)) {
                    CollabParticipant participant = new CollabParticipant();
                    participant.setSessionId(session.getSessionId());
                    participant.setUserId(invitee.getUserId());
                    participant.setPermission(parsePermission(invitee.getPermission()));
                    participantRepository.save(participant);
                }
            }
        }

        return SessionResponse.of(session);
    }

    // ============================ 7.2.2 我的协同会话列表 ============================

    public PageResponse<SessionListItem> listMySessions(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CollabSession> pageResult = sessionRepository.findMySessions(userId, pageable);

        // 预加载参与者计数
        Map<Integer, Integer> participantCounts = new HashMap<>();
        for (CollabSession session : pageResult.getContent()) {
            List<CollabParticipant> participants = participantRepository.findBySessionId(session.getSessionId());
            participantCounts.put(session.getSessionId(), participants.size());
        }

        return PageResponse.of(pageResult, page, size,
                session -> SessionListItem.of(session,
                        participantCounts.getOrDefault(session.getSessionId(), 1)));
    }

    // ============================ 7.2.3 会话详情 ============================

    public SessionDetailResponse getSessionDetail(Integer sessionId, Integer userId, String role) {
        CollabSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "协同会话不存在"));

        // 权限校验：必须是参与者或管理员
        boolean isAdmin = "admin".equalsIgnoreCase(role);
        boolean isParticipant = participantRepository.existsBySessionIdAndUserId(sessionId, userId);
        if (!isParticipant && !isAdmin) {
            throw new BusinessException(403, "您不是该会话的参与者，无权查看");
        }

        List<SessionParticipantResponse> participants = participantRepository.findBySessionId(sessionId)
                .stream()
                .map(SessionParticipantResponse::of)
                .collect(Collectors.toList());

        return SessionDetailResponse.of(session, participants);
    }

    // ============================ 7.2.4 邀请参与者 ============================

    @Transactional
    public SessionParticipantResponse inviteParticipant(Integer sessionId, Integer ownerId, InviteRequest request) {
        CollabSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "协同会话不存在"));

        if (!session.getOwnerId().equals(ownerId)) {
            throw new BusinessException(4031, "仅会话所有者可邀请参与者");
        }

        if (session.getStatus() != CollabSession.SessionStatus.active) {
            throw new BusinessException(400, "会话已关闭或归档，无法邀请");
        }

        // 通过 userId 或 inviteLink 确定目标用户
        Integer targetUserId = request.getUserId();
        if (targetUserId == null && request.getInviteLink() != null) {
            // inviteLink 格式: https://zaran.com/collab/invite/{shortToken}
            // 通过 shortToken 匹配 session
            String shortToken = extractShortToken(request.getInviteLink());
            if (shortToken == null || !session.getSessionToken().startsWith(shortToken)) {
                throw new BusinessException(400, "邀请链接无效");
            }
            // 通过邀请链接加入时，需要从当前用户上下文获取（这里简化处理，要求同时传 userId）
            throw new BusinessException(400, "通过邀请链接加入需提供 userId");
        }

        if (targetUserId == null) {
            throw new BusinessException(400, "userId 和 inviteLink 至少提供一个");
        }

        // 检查目标用户是否存在
        if (!userRepository.existsById(targetUserId)) {
            throw new BusinessException(4041, "用户不存在");
        }

        // 检查是否已是参与者
        if (participantRepository.existsBySessionIdAndUserId(sessionId, targetUserId)) {
            throw new BusinessException(409, "该用户已是会话参与者");
        }

        CollabParticipant participant = new CollabParticipant();
        participant.setSessionId(sessionId);
        participant.setUserId(targetUserId);
        participant.setPermission(parsePermission(request.getPermission()));
        participantRepository.save(participant);

        return SessionParticipantResponse.of(participant);
    }

    // ============================ 7.2.5 修改参与者权限 ============================

    @Transactional
    public SessionParticipantResponse updateParticipantPermission(Integer sessionId, Integer targetUserId,
                                                                  Integer ownerId, String newPermission) {
        CollabSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "协同会话不存在"));

        if (!session.getOwnerId().equals(ownerId)) {
            throw new BusinessException(4031, "仅会话所有者可修改权限");
        }

        // 不能修改自己的权限
        if (targetUserId.equals(ownerId)) {
            throw new BusinessException(400, "无法修改自己的权限");
        }

        CollabParticipant participant = participantRepository.findBySessionIdAndUserId(sessionId, targetUserId)
                .orElseThrow(() -> new BusinessException(404, "该用户不是会话参与者"));

        participant.setPermission(parsePermission(newPermission));
        participantRepository.save(participant);

        return SessionParticipantResponse.of(participant);
    }

    // ============================ 7.2.6 移除参与者 ============================

    @Transactional
    public void removeParticipant(Integer sessionId, Integer targetUserId, Integer ownerId) {
        CollabSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "协同会话不存在"));

        if (!session.getOwnerId().equals(ownerId)) {
            throw new BusinessException(4031, "仅会话所有者可移除参与者");
        }

        // 不能移除自己
        if (targetUserId.equals(ownerId)) {
            throw new BusinessException(400, "无法移除自己");
        }

        CollabParticipant participant = participantRepository.findBySessionIdAndUserId(sessionId, targetUserId)
                .orElseThrow(() -> new BusinessException(404, "该用户不是会话参与者"));

        participantRepository.delete(participant);
    }

    // ============================ 7.2.7 获取操作历史 ============================

    public OperationHistoryResponse getOperationHistory(Integer sessionId, Integer userId,
                                                        Integer versionStart, Integer versionEnd,
                                                        int page, int size) {
        CollabSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "协同会话不存在"));

        // 必须是参与者
        if (!participantRepository.existsBySessionIdAndUserId(sessionId, userId)) {
            throw new BusinessException(403, "您不是该会话的参与者，无权查看操作历史");
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<CollabOperation> pageResult;

        if (versionStart != null && versionEnd != null) {
            pageResult = operationRepository.findBySessionIdAndVersionNumberBetweenOrderByVersionNumberAsc(
                    sessionId, versionStart, versionEnd, pageable);
        } else {
            pageResult = operationRepository.findBySessionIdOrderByVersionNumberDesc(sessionId, pageable);
        }

        // 预加载用户名映射
        Map<Integer, String> usernameMap = loadUsernamesForOperations(pageResult.getContent());

        List<OperationHistoryResponse.OperationItem> operations = pageResult.getContent().stream()
                .map(op -> OperationHistoryResponse.OperationItem.of(
                        op,
                        usernameMap.getOrDefault(op.getUserId(), "未知用户"),
                        parseJson(op.getOpData())))
                .collect(Collectors.toList());

        OperationHistoryResponse response = new OperationHistoryResponse();
        response.setSessionId(sessionId);
        response.setCurrentVersion(session.getCurrentVersion());
        response.setOperations(operations);
        return response;
    }

    // ============================ 7.2.8 手动保存版本 ============================

    @Transactional
    public CollabVersionResponse saveVersion(Integer sessionId, Integer userId, SaveVersionRequest request) {
        CollabSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "协同会话不存在"));

        // 必须是参与者且有编辑权限
        CollabParticipant participant = participantRepository.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new BusinessException(403, "您不是该会话的参与者"));

        if (participant.getPermission() != CollabParticipant.CollabPermission.edit) {
            throw new BusinessException(403, "您没有编辑权限，无法保存版本");
        }

        int nextVersionNo = versionRepository.findTopBySessionIdOrderByVersionNumberDesc(sessionId)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        CollabSessionVersion version = new CollabSessionVersion();
        version.setSessionId(sessionId);
        version.setUserId(userId);
        version.setVersionNumber(nextVersionNo);
        version.setChangeDesc(request.getChangeDesc());
        if (request.getLayersJson() != null) {
            version.setLayersJson(writeJson(request.getLayersJson()));
        }
        versionRepository.save(version);

        // 更新会话当前版本号
        session.setCurrentVersion(nextVersionNo);
        sessionRepository.save(session);

        return CollabVersionResponse.of(version);
    }

    // ============================ 7.2.9 回滚到版本 ============================

    @Transactional
    public CollabVersionResponse rollbackVersion(Integer sessionId, Integer versionId, Integer ownerId) {
        CollabSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "协同会话不存在"));

        if (!session.getOwnerId().equals(ownerId)) {
            throw new BusinessException(4031, "仅会话所有者可回滚版本");
        }

        CollabSessionVersion target = versionRepository.findByVersionIdAndSessionId(versionId, sessionId)
                .orElseThrow(() -> new BusinessException(404, "目标版本不存在"));

        // 以最新版本号+1 生成一条新版本快照（标记为回滚），保证历史可追溯
        int nextVersionNo = versionRepository.findTopBySessionIdOrderByVersionNumberDesc(sessionId)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        CollabSessionVersion rollbackSnapshot = new CollabSessionVersion();
        rollbackSnapshot.setSessionId(sessionId);
        rollbackSnapshot.setUserId(ownerId);
        rollbackSnapshot.setVersionNumber(nextVersionNo);
        rollbackSnapshot.setChangeDesc("回滚到版本 " + target.getVersionNumber());
        rollbackSnapshot.setLayersJson(target.getLayersJson());
        versionRepository.save(rollbackSnapshot);

        // 更新会话当前版本号
        session.setCurrentVersion(nextVersionNo);
        sessionRepository.save(session);

        return CollabVersionResponse.of(rollbackSnapshot);
    }

    // ============================ WebSocket 辅助方法 ============================

    /**
     * 记录一次操作（由 WebSocket Handler 调用）。
     */
    @Transactional
    public CollabOperation recordOperation(Integer sessionId, Integer userId, String opType, JsonNode opData) {
        CollabSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "协同会话不存在"));

        int nextVersion = operationRepository.findTopBySessionIdOrderByVersionNumberDesc(sessionId)
                .map(op -> op.getVersionNumber() + 1)
                .orElse(1);

        CollabOperation operation = new CollabOperation();
        operation.setSessionId(sessionId);
        operation.setUserId(userId);
        operation.setOpType(opType);
        operation.setOpData(writeJson(opData));
        operation.setVersionNumber(nextVersion);
        operationRepository.save(operation);

        // 更新会话版本号
        session.setCurrentVersion(nextVersion);
        sessionRepository.save(session);

        return operation;
    }

    /**
     * 关闭会话（由所有者操作）。
     */
    @Transactional
    public void closeSession(Integer sessionId, Integer ownerId) {
        CollabSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new BusinessException(404, "协同会话不存在"));

        if (!session.getOwnerId().equals(ownerId)) {
            throw new BusinessException(4031, "仅会话所有者可关闭会话");
        }

        session.setStatus(CollabSession.SessionStatus.closed);
        session.setClosedAt(LocalDateTime.now());
        sessionRepository.save(session);
    }

    // ============================ 辅助方法 ============================

    /** 解析权限字符串 */
    private CollabParticipant.CollabPermission parsePermission(String permission) {
        if (permission == null) {
            return CollabParticipant.CollabPermission.view;
        }
        return switch (permission.toLowerCase()) {
            case "edit" -> CollabParticipant.CollabPermission.edit;
            case "comment" -> CollabParticipant.CollabPermission.comment;
            default -> CollabParticipant.CollabPermission.view;
        };
    }

    /** 从邀请链接中提取短 token */
    private String extractShortToken(String inviteLink) {
        if (inviteLink == null) return null;
        int lastSlash = inviteLink.lastIndexOf('/');
        if (lastSlash >= 0 && lastSlash < inviteLink.length() - 1) {
            return inviteLink.substring(lastSlash + 1);
        }
        return null;
    }

    /** 批量加载用户名映射 */
    private Map<Integer, String> loadUsernamesForOperations(List<CollabOperation> operations) {
        Map<Integer, String> map = new HashMap<>();
        for (CollabOperation op : operations) {
            if (!map.containsKey(op.getUserId())) {
                userRepository.findById(op.getUserId())
                        .ifPresent(user -> map.put(op.getUserId(), user.getUsername()));
            }
        }
        return map;
    }

    private JsonNode parseJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String writeJson(JsonNode node) {
        if (node == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new BusinessException(500, "JSON 序列化失败");
        }
    }
}
