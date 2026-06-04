package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.User;

public class RegisterResponse {

    private Integer userId;
    private String username;
    private String role;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    public static RegisterResponse of(User user, String accessToken, String refreshToken, long expiresIn) {
        RegisterResponse response = new RegisterResponse();
        response.userId = user.getUserId();
        response.username = user.getUsername();
        response.role = user.getRole().name();
        response.accessToken = accessToken;
        response.refreshToken = refreshToken;
        response.expiresIn = expiresIn;
        return response;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
}
