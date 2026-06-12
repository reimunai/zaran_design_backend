package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 协同编辑会话，对应数据库 collab_sessions 表。
 * 记录协同编辑项目的会话信息与状态。
 */
@Entity
@Table(name = "collab_sessions")
public class CollabSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Integer sessionId;

    /** 关联图案ID（新建空白画布时为null） */
    @Column(name = "pattern_id")
    private Integer patternId;

    /** 创建者（会话所有者） */
    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    /** 会话名称 */
    @Column(name = "session_name", nullable = false, length = 100)
    private String sessionName;

    /** WebSocket 会话唯一标识（UUID） */
    @Column(name = "session_token", nullable = false, unique = true, length = 64)
    private String sessionToken;

    /** 会话状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SessionStatus status = SessionStatus.active;

    /** 当前版本号，随手动保存版本递增 */
    @Column(name = "current_version", nullable = false)
    private Integer currentVersion = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    public enum SessionStatus {
        active, archived, closed
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (sessionToken == null || sessionToken.isBlank()) {
            sessionToken = UUID.randomUUID().toString().replace("-", "");
        }
    }

    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

    public Integer getPatternId() { return patternId; }
    public void setPatternId(Integer patternId) { this.patternId = patternId; }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }

    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }

    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }

    public SessionStatus getStatus() { return status; }
    public void setStatus(SessionStatus status) { this.status = status; }

    public Integer getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(Integer currentVersion) { this.currentVersion = currentVersion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
}
