package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.dto.SketchListItem.CategoryBrief;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.List;

/** 草图详情响应，含 layersJson 图层数据 */
public class SketchDetailResponse {

    private Integer sketchId;
    private String name;
    private String thumbnailUrl;
    private JsonNode layersJson;
    private Integer width;
    private Integer height;
    private Boolean isPublic;
    private Boolean isOwner;
    private Integer currentVersion;
    private CategoryBrief category;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Integer getSketchId() { return sketchId; }
    public void setSketchId(Integer sketchId) { this.sketchId = sketchId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public JsonNode getLayersJson() { return layersJson; }
    public void setLayersJson(JsonNode layersJson) { this.layersJson = layersJson; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    public Boolean getIsOwner() { return isOwner; }
    public void setIsOwner(Boolean isOwner) { this.isOwner = isOwner; }
    public Integer getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(Integer currentVersion) { this.currentVersion = currentVersion; }
    public CategoryBrief getCategory() { return category; }
    public void setCategory(CategoryBrief category) { this.category = category; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
