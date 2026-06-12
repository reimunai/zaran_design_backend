package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.User;

import java.time.LocalDateTime;

/**
 * 管理员视角的用户详情 DTO（含管理员才可见的信息）。
 */
public class AdminUserDetailResponse {

    private Integer userId;
    private String username;
    private String role;
    private String phone;
    private String email;
    private String avatar;
    private String bio;
    private Integer status;
    private String professionalField;
    private Integer yearsOfExperience;
    private String portfolioUrl;
    private String certificationStatus;
    private long patternCount;
    private long followerCount;
    private long followingCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AdminUserDetailResponse of(User user, long patternCount, long followerCount, long followingCount) {
        AdminUserDetailResponse r = new AdminUserDetailResponse();
        r.userId = user.getUserId();
        r.username = user.getUsername();
        r.role = user.getRole().name();
        r.phone = user.getPhone();
        r.email = user.getEmail();
        r.avatar = user.getAvatar();
        r.bio = user.getBio();
        r.status = Boolean.TRUE.equals(user.getIsDisabled()) ? 0 : 1;
        r.professionalField = user.getProfessionalField();
        r.yearsOfExperience = user.getYearsOfExperience();
        r.portfolioUrl = user.getPortfolioUrl();
        r.certificationStatus = user.getCertificationStatus().name();
        r.patternCount = patternCount;
        r.followerCount = followerCount;
        r.followingCount = followingCount;
        r.createdAt = user.getCreatedAt();
        r.updatedAt = user.getUpdatedAt();
        return r;
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
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getProfessionalField() { return professionalField; }
    public void setProfessionalField(String professionalField) { this.professionalField = professionalField; }
    public Integer getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(Integer yearsOfExperience) { this.yearsOfExperience = yearsOfExperience; }
    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }
    public String getCertificationStatus() { return certificationStatus; }
    public void setCertificationStatus(String certificationStatus) { this.certificationStatus = certificationStatus; }
    public long getPatternCount() { return patternCount; }
    public void setPatternCount(long patternCount) { this.patternCount = patternCount; }
    public long getFollowerCount() { return followerCount; }
    public void setFollowerCount(long followerCount) { this.followerCount = followerCount; }
    public long getFollowingCount() { return followingCount; }
    public void setFollowingCount(long followingCount) { this.followingCount = followingCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
