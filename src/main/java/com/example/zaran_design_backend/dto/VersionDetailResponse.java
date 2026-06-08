package com.example.zaran_design_backend.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

/** 指定版本详情响应，含 layersJson */
public class VersionDetailResponse {

    private Integer versionId;
    private Integer versionNo;
    private String changeDesc;
    private String thumbnailUrl;
    private JsonNode layersJson;
    private LocalDateTime createdAt;

    public Integer getVersionId() { return versionId; }
    public void setVersionId(Integer versionId) { this.versionId = versionId; }
    public Integer getVersionNo() { return versionNo; }
    public void setVersionNo(Integer versionNo) { this.versionNo = versionNo; }
    public String getChangeDesc() { return changeDesc; }
    public void setChangeDesc(String changeDesc) { this.changeDesc = changeDesc; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public JsonNode getLayersJson() { return layersJson; }
    public void setLayersJson(JsonNode layersJson) { this.layersJson = layersJson; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
