package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.CollabSession;

import java.time.LocalDateTime;

/**
 * 协同会话响应（7.2.1 创建会话返回 / 7.2.3 会话详情返回）
 */
public class SessionResponse {

    private Integer sessionId;
    private String sessionName;
    private Integer patternId;
    private Integer ownerId;
    private String status;
    private String inviteLink;
    private String websocketUrl;
    private Integer currentVersion;
    private LocalDateTime createdAt;

    public static SessionResponse of(CollabSession session) {
        SessionResponse r = new SessionResponse();
        r.sessionId = session.getSessionId();
        r.sessionName = session.getSessionName();
        r.patternId = session.getPatternId();
        r.ownerId = session.getOwnerId();
        r.status = session.getStatus().name();
        r.inviteLink = "https://zaran.com/collab/invite/" + session.getSessionToken().substring(0, 8);
        r.websocketUrl = "wss://api.zaran.com/ws/collab/" + session.getSessionId();
        r.currentVersion = session.getCurrentVersion();
        r.createdAt = session.getCreatedAt();
        return r;
    }

    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }

    public Integer getPatternId() { return patternId; }
    public void setPatternId(Integer patternId) { this.patternId = patternId; }

    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getInviteLink() { return inviteLink; }
    public void setInviteLink(String inviteLink) { this.inviteLink = inviteLink; }

    public String getWebsocketUrl() { return websocketUrl; }
    public void setWebsocketUrl(String websocketUrl) { this.websocketUrl = websocketUrl; }

    public Integer getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(Integer currentVersion) { this.currentVersion = currentVersion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
