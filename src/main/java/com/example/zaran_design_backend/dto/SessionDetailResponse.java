package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.CollabSession;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 协同会话详情响应（7.2.3）
 */
public class SessionDetailResponse {

    private Integer sessionId;
    private String sessionName;
    private Integer patternId;
    private Integer ownerId;
    private String status;
    private String inviteLink;
    private String websocketUrl;
    private Integer currentVersion;
    private List<SessionParticipantResponse> participants;
    private LocalDateTime createdAt;
    private LocalDateTime closedAt;

    public static SessionDetailResponse of(CollabSession session, List<SessionParticipantResponse> participants) {
        SessionDetailResponse r = new SessionDetailResponse();
        r.sessionId = session.getSessionId();
        r.sessionName = session.getSessionName();
        r.patternId = session.getPatternId();
        r.ownerId = session.getOwnerId();
        r.status = session.getStatus().name();
        r.inviteLink = "https://zaran.com/collab/invite/" + session.getSessionToken().substring(0, 8);
        r.websocketUrl = "wss://api.zaran.com/ws/collab/" + session.getSessionId();
        r.currentVersion = session.getCurrentVersion();
        r.participants = participants;
        r.createdAt = session.getCreatedAt();
        r.closedAt = session.getClosedAt();
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

    public List<SessionParticipantResponse> getParticipants() { return participants; }
    public void setParticipants(List<SessionParticipantResponse> participants) { this.participants = participants; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }
}
