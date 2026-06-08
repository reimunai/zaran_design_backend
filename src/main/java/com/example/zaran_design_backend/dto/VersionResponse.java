package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.SketchVersion;

import java.time.LocalDateTime;

/** 版本历史列表项（不含 layersJson） */
public class VersionResponse {

    private Integer versionId;
    private Integer versionNo;
    private String changeDesc;
    private String thumbnailUrl;
    private LocalDateTime createdAt;

    public static VersionResponse of(SketchVersion version) {
        VersionResponse response = new VersionResponse();
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
