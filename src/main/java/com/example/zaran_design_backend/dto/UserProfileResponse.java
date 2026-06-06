package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.User;
import com.example.zaran_design_backend.entity.UserCertificationRequest;

import java.time.LocalDateTime;

public class UserProfileResponse {

    private Integer userId;
    private String username;
    private String role;
    private String phone;
    private String email;
    private String avatar;
    private String bio;
    private DesignerProfile designerProfile;
    private UserStats stats;

    public static UserProfileResponse of(User user, long patternCount, long favoriteCount,
                                          long followerCount, long followingCount) {
        UserProfileResponse response = new UserProfileResponse();
        response.userId = user.getUserId();
        response.username = user.getUsername();
        response.role = user.getRole().name();
        response.phone = maskPhone(user.getPhone());
        response.email = user.getEmail();
        response.avatar = user.getAvatar();
        response.bio = user.getBio();
        response.designerProfile = new DesignerProfile(user);
        response.stats = new UserStats(patternCount, favoriteCount, followerCount, followingCount);
        return response;
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
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public DesignerProfile getDesignerProfile() { return designerProfile; }
    public void setDesignerProfile(DesignerProfile designerProfile) { this.designerProfile = designerProfile; }
    public UserStats getStats() { return stats; }
    public void setStats(UserStats stats) { this.stats = stats; }

    public static class DesignerProfile {
        private String professionField;
        private Integer experienceYears;
        private String portfolioUrl;
        private String certificationStatus;
        private String reviewerName;

        public DesignerProfile(User user) {
            this.professionField = user.getProfessionalField();
            this.experienceYears = user.getYearsOfExperience();
            this.portfolioUrl = user.getPortfolioUrl();
            this.certificationStatus = user.getCertificationStatus().name();
            this.reviewerName = null; // 需要查询审核人姓名，简化处理
        }

        public String getProfessionField() { return professionField; }
        public void setProfessionField(String professionField) { this.professionField = professionField; }
        public Integer getExperienceYears() { return experienceYears; }
        public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }
        public String getPortfolioUrl() { return portfolioUrl; }
        public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }
        public String getCertificationStatus() { return certificationStatus; }
        public void setCertificationStatus(String certificationStatus) { this.certificationStatus = certificationStatus; }
        public String getReviewerName() { return reviewerName; }
        public void setReviewerName(String reviewerName) { this.reviewerName = reviewerName; }
    }

    public static class UserStats {
        private long patternCount;
        private long favoriteCount;
        private long followerCount;
        private long followingCount;

        public UserStats(long patternCount, long favoriteCount, long followerCount, long followingCount) {
            this.patternCount = patternCount;
            this.favoriteCount = favoriteCount;
            this.followerCount = followerCount;
            this.followingCount = followingCount;
        }

        public long getPatternCount() { return patternCount; }
        public void setPatternCount(long patternCount) { this.patternCount = patternCount; }
        public long getFavoriteCount() { return favoriteCount; }
        public void setFavoriteCount(long favoriteCount) { this.favoriteCount = favoriteCount; }
        public long getFollowerCount() { return followerCount; }
        public void setFollowerCount(long followerCount) { this.followerCount = followerCount; }
        public long getFollowingCount() { return followingCount; }
        public void setFollowingCount(long followingCount) { this.followingCount = followingCount; }
    }
}
