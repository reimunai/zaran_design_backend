package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.CollabSession;

import java.time.LocalDateTime;

/**
 * 协同会话列表项（7.2.2 我的协同会话列表）
 */
public class SessionListItem {

    private Integer sessionId;
    private String sessionName;
    private Integer patternId;
    private String status;
    private Integer ownerId;
    private Integer participantCount;
    private Integer currentVersion;
    private LocalDateTime createdAt;

    public static SessionListItem of(CollabSession session, int participantCount) {
        SessionListItem item = new SessionListItem();
        item.sessionId = session.getSessionId();
        item.sessionName = session.getSessionName();
        item.patternId = session.getPatternId();
        item.status = session.getStatus().name();
        item.ownerId = session.getOwnerId();
        item.participantCount = participantCount;
        item.currentVersion = session.getCurrentVersion();
        item.createdAt = session.getCreatedAt();
        return item;
    }

    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }

    public Integer getPatternId() { return patternId; }
    public void setPatternId(Integer patternId) { this.patternId = patternId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }

    public Integer getParticipantCount() { return participantCount; }
    public void setParticipantCount(Integer participantCount) { this.participantCount = participantCount; }

    public Integer getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(Integer currentVersion) { this.currentVersion = currentVersion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
