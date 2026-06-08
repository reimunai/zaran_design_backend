package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.SketchVersion;

import java.time.LocalDateTime;

/** 创建版本快照响应 */
public class CreateVersionResponse {

    private Integer versionId;
    private Integer versionNo;
    private String changeDesc;
    private String thumbnailUrl;
    private LocalDateTime createdAt;

    public static CreateVersionResponse of(SketchVersion version) {
        CreateVersionResponse response = new CreateVersionResponse();
        response.versionId = version.getVersionId();
        response.versionNo = version.getVersionNumber();
        response.changeDesc = version.getChangeDesc();
        response.thumbnailUrl = version.getThumbnailPath();
        response.createdAt = version.getCreatedAt();
        return response;
    }

    public Integer getVersionId() { return versionId; }
    public void setVersionId(Integer versionId) { this.versionId = versionId; }
    public Integer getVersionNo() { return versionNo; }
    public void setVersionNo(Integer versionNo) { this.versionNo = versionNo; }
    public String getChangeDesc() { return changeDesc; }
    public void setChangeDesc(String changeDesc) { this.changeDesc = changeDesc; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
