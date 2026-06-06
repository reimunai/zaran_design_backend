package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.User;
import com.example.zaran_design_backend.entity.UserCertificationRequest;

import java.time.LocalDateTime;

public class ApplicationResponse {

    private Integer applicationId;
    private Integer userId;
    private String username;
    private String targetRole;
    private String reason;
    private String status;
    private String reviewerComment;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;

    public static ApplicationResponse of(UserCertificationRequest request, User user) {
        ApplicationResponse response = new ApplicationResponse();
        response.applicationId = request.getRequestId();
        response.userId = user.getUserId();
        response.username = user.getUsername();
        response.targetRole = request.getTargetRole().name();
        response.reason = request.getReason();
        response.status = request.getStatus().name();
        response.reviewerComment = request.getReviewComment();
        response.submittedAt = request.getCreatedAt();
        response.reviewedAt = request.getReviewedAt();
        return response;
    }

    public Integer getApplicationId() { return applicationId; }
    public void setApplicationId(Integer applicationId) { this.applicationId = applicationId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReviewerComment() { return reviewerComment; }
    public void setReviewerComment(String reviewerComment) { this.reviewerComment = reviewerComment; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
