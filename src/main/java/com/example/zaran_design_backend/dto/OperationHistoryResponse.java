package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.CollabOperation;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作历史响应（7.2.3 获取操作历史）
 */
public class OperationHistoryResponse {

    private Integer sessionId;
    private Integer currentVersion;
    private List<OperationItem> operations;

    public static class OperationItem {
        private Integer opId;
        private Integer userId;
        private String username;
        private String opType;
        private JsonNode opData;
        private Integer version;
        private Long timestamp;

        public static OperationItem of(CollabOperation op, String username, JsonNode opData) {
            OperationItem item = new OperationItem();
            item.opId = op.getOpId();
            item.userId = op.getUserId();
            item.username = username;
            item.opType = op.getOpType();
            item.opData = opData;
            item.version = op.getVersionNumber();
            item.timestamp = op.getCreatedAt() != null
                    ? op.getCreatedAt().atZone(java.time.ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli()
                    : null;
            return item;
        }

        public Integer getOpId() { return opId; }
        public void setOpId(Integer opId) { this.opId = opId; }

        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getOpType() { return opType; }
        public void setOpType(String opType) { this.opType = opType; }

        public JsonNode getOpData() { return opData; }
        public void setOpData(JsonNode opData) { this.opData = opData; }

        public Integer getVersion() { return version; }
        public void setVersion(Integer version) { this.version = version; }

        public Long getTimestamp() { return timestamp; }
        public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
    }

    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

    public Integer getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(Integer currentVersion) { this.currentVersion = currentVersion; }

    public List<OperationItem> getOperations() { return operations; }
    public void setOperations(List<OperationItem> operations) { this.operations = operations; }
}
