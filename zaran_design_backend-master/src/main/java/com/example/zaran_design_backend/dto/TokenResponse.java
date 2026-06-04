package com.example.zaran_design_backend.dto;

public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    public static TokenResponse of(String accessToken, String refreshToken, long expiresIn) {
        TokenResponse response = new TokenResponse();
        response.accessToken = accessToken;
        response.refreshToken = refreshToken;
        response.expiresIn = expiresIn;
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
}
