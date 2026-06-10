package com.example.zaran_design_backend.dto;

/**
 * 处理评论请求（审核举报的评论）。
 */
public class AuditCommentRequest {

    /** approve(保留) / reject(删除) */
    private String action;

    /** 处理说明 */
    private String reason;

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
