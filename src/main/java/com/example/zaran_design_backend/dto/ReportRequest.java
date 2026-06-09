package com.example.zaran_design_backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 6.3.6 举报作品 — 请求参数
 */
public class ReportRequest {

    /** 举报原因：plagiarism / inappropriate / other */
    @NotBlank(message = "举报原因不能为空")
    private String reason;

    /** 补充说明 */
    private String description;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
