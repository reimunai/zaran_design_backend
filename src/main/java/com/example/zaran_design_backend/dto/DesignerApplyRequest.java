package com.example.zaran_design_backend.dto;

import jakarta.validation.constraints.NotBlank;

public class DesignerApplyRequest {

    @NotBlank(message = "申请理由不能为空")
    private String reason;

    private String portfolioUrl;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }
}
