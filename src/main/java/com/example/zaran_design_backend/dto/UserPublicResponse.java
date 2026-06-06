package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.User;

public class UserPublicResponse {

    private Integer userId;
    private String username;
    private String role;
    private String avatar;
    private String bio;
    private boolean isFollowing;
    private UserStats stats;

    public static UserPublicResponse of(User user, boolean isFollowing, long patternCount, long followerCount) {
        UserPublicResponse response = new UserPublicResponse();
        response.userId = user.getUserId();
        response.username = user.getUsername();
        response.role = user.getRole().name();
        response.avatar = user.getAvatar();
        response.bio = user.getBio();
        response.isFollowing = isFollowing;
        response.stats = new UserStats(patternCount, followerCount);
        return response;
    }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public boolean getIsFollowing() { return isFollowing; }
    public void setIsFollowing(boolean isFollowing) { this.isFollowing = isFollowing; }
    public UserStats getStats() { return stats; }
    public void setStats(UserStats stats) { this.stats = stats; }

    public static class UserStats {
        private long patternCount;
        private long followerCount;

        public UserStats(long patternCount, long followerCount) {
            this.patternCount = patternCount;
            this.followerCount = followerCount;
        }

        public long getPatternCount() { return patternCount; }
        public void setPatternCount(long patternCount) { this.patternCount = patternCount; }
        public long getFollowerCount() { return followerCount; }
        public void setFollowerCount(long followerCount) { this.followerCount = followerCount; }
    }
}
