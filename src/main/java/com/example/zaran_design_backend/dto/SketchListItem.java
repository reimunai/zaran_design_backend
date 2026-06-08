package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.Sketch;

import java.time.LocalDateTime;

/** 草图列表项（不含 layersJson，仅缩略图与元数据） */
public class SketchListItem {

    private Integer sketchId;
    private String name;
    private String thumbnailUrl;
    private Integer width;
    private Integer height;
    private Boolean isPublic;
    private CategoryBrief category;
    private Integer currentVersion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** category 由 service 解析后传入，可能为 null */
    public static SketchListItem of(Sketch sketch, CategoryBrief category) {
        SketchListItem item = new SketchListItem();
        item.sketchId = sketch.getSketchId();
        item.name = sketch.getName();
        item.thumbnailUrl = sketch.getThumbnailPath();
        item.width = sketch.getWidth();
        item.height = sketch.getHeight();
        item.isPublic = sketch.getIsPublic();
        item.category = category;
        item.currentVersion = sketch.getCurrentVersion();
        item.createdAt = sketch.getCreatedAt();
        item.updatedAt = sketch.getUpdatedAt();
        return item;
    }

    public Integer getSketchId() { return sketchId; }
    public void setSketchId(Integer sketchId) { this.sketchId = sketchId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    public CategoryBrief getCategory() { return category; }
    public void setCategory(CategoryBrief category) { this.category = category; }
    public Integer getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(Integer currentVersion) { this.currentVersion = currentVersion; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /** 简要分类信息 {categoryId, name} */
    public static class CategoryBrief {
        private Integer categoryId;
        private String name;

        public CategoryBrief() {
        }

        public CategoryBrief(Integer categoryId, String name) {
            this.categoryId = categoryId;
            this.name = name;
        }

        public Integer getCategoryId() { return categoryId; }
        public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
