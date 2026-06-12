package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.CollabSessionVersion;

import java.time.LocalDateTime;

/**
 * 协同编辑版本响应
 */
public class CollabVersionResponse {

    private Integer versionId;
    private Integer versionNumber;
    private Integer userId;
    private String changeDesc;
    private LocalDateTime createdAt;

    public static CollabVersionResponse of(CollabSessionVersion v) {
        CollabVersionResponse r = new CollabVersionResponse();
        r.versionId = v.getVersionId();
        r.versionNumber = v.getVersionNumber();
        r.userId = v.getUserId();
        r.changeDesc = v.getChangeDesc();
        r.createdAt = v.getCreatedAt();
        return r;
    }

    public Integer getVersionId() { return versionId; }
    public void setVersionId(Integer versionId) { this.versionId = versionId; }

    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getChangeDesc() { return changeDesc; }
    public void setChangeDesc(String changeDesc) { this.changeDesc = changeDesc; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
