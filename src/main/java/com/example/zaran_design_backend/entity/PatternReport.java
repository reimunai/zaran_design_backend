package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 作品举报记录，对应数据库 pattern_reports 表。
 */
@Entity
@Table(name = "pattern_reports",
       uniqueConstraints = @UniqueConstraint(columnNames = {"pattern_id", "user_id"}))
public class PatternReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @Column(name = "pattern_id", nullable = false)
    private Integer patternId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** 举报原因：plagiarism / inappropriate / other */
    @Column(nullable = false, length = 50)
    private String reason;

    /** 补充说明 */
    @Lob
    @Column
    private String description;

    /** 处理状态：pending / resolved */
    @Column(nullable = false, length = 20)
    private String status = "pending";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Integer getReportId() { return reportId; }
    public void setReportId(Integer reportId) { this.reportId = reportId; }

    public Integer getPatternId() { return patternId; }
    public void setPatternId(Integer patternId) { this.patternId = patternId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
