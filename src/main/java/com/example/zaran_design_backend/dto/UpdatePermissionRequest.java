package com.example.zaran_design_backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 修改参与者权限请求（7.2.5）
 */
public class UpdatePermissionRequest {

    @NotBlank(message = "权限不能为空")
    private String permission;

    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }
}
