package com.example.zaran_design_backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 邀请参与者请求（7.2.2）
 */
public class InviteRequest {

    /** 用户ID（与inviteLink二选一） */
    private Integer userId;

    /** 邀请链接（与userId二选一） */
    private String inviteLink;

    @NotBlank(message = "权限不能为空")
    private String permission;

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getInviteLink() { return inviteLink; }
    public void setInviteLink(String inviteLink) { this.inviteLink = inviteLink; }

    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
}
