package com.example.zaran_design_backend.dto;

/**
 * 审核作品请求。
 */
public class AuditPatternRequest {

    /** approve(通过) / reject(下架) */
    private String action;

    /** 下架原因（reject时填写） */
    private String reason;

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
