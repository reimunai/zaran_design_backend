package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 草图主记录，对应数据库 sketches 表。
 * 图层矢量数据统一以 JSON 字符串形式存储在 layers_json 字段；删除采用软删除（写入 deleted_at）。
 */
@Entity
@Table(name = "sketches")
public class Sketch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sketch_id")
    private Integer sketchId;

    /** 所有者用户ID */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Integer width;

    @Column(nullable = false)
    private Integer height;

    @Column(name = "category_id")
    private Integer categoryId;

    /** 图层结构数据（JSON 字符串） */
    @Column(name = "layers_json", columnDefinition = "LONGTEXT")
    private String layersJson;

    /** 标签，逗号分隔存储 */
    @Column(length = 500)
    private String tags;

    /** 缩略图地址（无 MinIO 环境下存储 data URL 或占位地址） */
    @Column(name = "thumbnail_path", columnDefinition = "LONGTEXT")
    private String thumbnailPath;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    /** 当前版本号，随版本快照递增 */
    @Column(name = "current_version", nullable = false)
    private Integer currentVersion = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 软删除标记，非空表示已删除（在回收站中） */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getSketchId() { return sketchId; }
    public void setSketchId(Integer sketchId) { this.sketchId = sketchId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getLayersJson() { return layersJson; }
    public void setLayersJson(String layersJson) { this.layersJson = layersJson; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public Integer getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(Integer currentVersion) { this.currentVersion = currentVersion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}
