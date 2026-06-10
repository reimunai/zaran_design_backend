package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.User;

import java.time.LocalDateTime;

/**
 * 管理员用户列表项 DTO。
 */
public class AdminUserItem {

    private Integer userId;
    private String username;
    private String role;
    private String phone;
    private String email;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public static AdminUserItem of(User user) {
        AdminUserItem item = new AdminUserItem();
        item.userId = user.getUserId();
        item.username = user.getUsername();
        item.role = user.getRole().name();
        item.phone = maskPhone(user.getPhone());
        item.email = user.getEmail();
        item.status = Boolean.TRUE.equals(user.getIsDisabled()) ? 0 : 1;
        item.createdAt = user.getCreatedAt();
        item.lastLoginAt = user.getUpdatedAt(); // 暂用 updatedAt 近似
        return item;
    }

    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
