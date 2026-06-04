package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.User;

public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private String tokenType = "Bearer";
    private UserInfo userInfo;

    public static LoginResponse of(String accessToken, String refreshToken, long expiresIn, User user) {
        LoginResponse response = new LoginResponse();
        response.accessToken = accessToken;
        response.refreshToken = refreshToken;
        response.expiresIn = expiresIn;
        response.userInfo = new UserInfo(user);
        return response;
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

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public static class UserInfo {
        private Integer userId;
        private String username;
        private String role;
        private String avatar;

        public UserInfo(User user) {
            this.userId = user.getUserId();
            this.username = user.getUsername();
            this.role = user.getRole().name();
            this.avatar = user.getAvatar();
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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
