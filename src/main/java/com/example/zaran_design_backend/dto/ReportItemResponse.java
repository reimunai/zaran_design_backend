package com.example.zaran_design_backend.dto;

import java.time.LocalDateTime;

/**
 * 举报记录列表项 DTO。
 */
public class ReportItemResponse {

    private Integer reportId;
    private Integer patternId;
    private String patternName;
    private Integer userId;
    private String username;
    private String reason;
    private String description;
    private String status;
    private LocalDateTime createdAt;

    public static ReportItemResponse of(Integer reportId, Integer patternId, String patternName,
                                         Integer userId, String username, String reason,
                                         String description, String status, LocalDateTime createdAt) {
        ReportItemResponse r = new ReportItemResponse();
        r.reportId = reportId;
        r.patternId = patternId;
        r.patternName = patternName;
        r.userId = userId;
        r.username = username;
        r.reason = reason;
        r.description = description;
        r.status = status;
        r.createdAt = createdAt;
        return r;
    }

    public Integer getReportId() { return reportId; }
    public void setReportId(Integer reportId) { this.reportId = reportId; }
    public Integer getPatternId() { return patternId; }
    public void setPatternId(Integer patternId) { this.patternId = patternId; }
    public String getPatternName() { return patternName; }
    public void setPatternName(String patternName) { this.patternName = patternName; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
