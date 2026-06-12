package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 协同参与人，对应数据库 collab_participants 表。
 * 记录会话中每个参与者的权限信息。
 */
@Entity
@Table(name = "collab_participants",
       uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "user_id"}))
public class CollabParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Integer participantId;

    @Column(name = "session_id", nullable = false)
    private Integer sessionId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** 权限：view（查看）/ comment（批注）/ edit（编辑） */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CollabPermission permission = CollabPermission.view;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    public enum CollabPermission {
        view, comment, edit
    }

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }

    public Integer getParticipantId() { return participantId; }
    public void setParticipantId(Integer participantId) { this.participantId = participantId; }

    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public CollabPermission getPermission() { return permission; }
    public void setPermission(CollabPermission permission) { this.permission = permission; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
