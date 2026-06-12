package com.example.zaran_design_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 创建协同会话请求（7.2.1）
 */
public class CreateSessionRequest {

    /** 关联图案ID，新建空白画布时为null */
    private Long patternId;

    @NotBlank(message = "会话名称不能为空")
    @Size(max = 100, message = "会话名称最多100字")
    private String sessionName;

    /** 邀请用户列表 */
    private List<Invitee> invitees;

    public static class Invitee {
        private Integer userId;
        private String permission;

        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }

        public String getPermission() { return permission; }
        public void setPermission(String permission) { this.permission = permission; }
    }

    public Long getPatternId() { return patternId; }
    public void setPatternId(Long patternId) { this.patternId = patternId; }

    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }

    public List<Invitee> getInvitees() { return invitees; }
    public void setInvitees(List<Invitee> invitees) { this.invitees = invitees; }
}
