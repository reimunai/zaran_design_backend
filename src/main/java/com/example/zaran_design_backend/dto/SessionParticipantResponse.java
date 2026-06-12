package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.CollabParticipant;

import java.time.LocalDateTime;

/**
 * 会话参与者响应
 */
public class SessionParticipantResponse {

    private Integer participantId;
    private Integer userId;
    private String permission;
    private LocalDateTime joinedAt;

    public static SessionParticipantResponse of(CollabParticipant p) {
        SessionParticipantResponse r = new SessionParticipantResponse();
        r.participantId = p.getParticipantId();
        r.userId = p.getUserId();
        r.permission = p.getPermission().name();
        r.joinedAt = p.getJoinedAt();
        return r;
    }

    public Integer getParticipantId() { return participantId; }
    public void setParticipantId(Integer participantId) { this.participantId = participantId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}
