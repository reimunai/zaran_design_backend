package com.example.zaran_design_backend.dto;

import java.time.LocalDateTime;

/**
 * 操作日志列表项 DTO。
 */
public class LogItemResponse {

    private Integer logId;
    private Integer userId;
    private String username;
    private String operationType;
    private String operationContent;
    private String ipAddress;
    private String userAgent;
    private String result;
    private LocalDateTime createdAt;

    public static LogItemResponse of(Integer logId, Integer userId, String username,
                                     String operationType, String operationContent,
                                     String ipAddress, String userAgent, String result,
                                     LocalDateTime createdAt) {
        LogItemResponse r = new LogItemResponse();
        r.logId = logId;
        r.userId = userId;
        r.username = username;
        r.operationType = operationType;
        r.operationContent = operationContent;
        r.ipAddress = ipAddress;
        r.userAgent = userAgent;
        r.result = result;
        r.createdAt = createdAt;
        return r;
    }

    public Integer getLogId() { return logId; }
    public void setLogId(Integer logId) { this.logId = logId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public String getOperationContent() { return operationContent; }
    public void setOperationContent(String operationContent) { this.operationContent = operationContent; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
