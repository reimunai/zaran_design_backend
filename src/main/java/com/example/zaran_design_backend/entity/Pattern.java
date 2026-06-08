package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 图案作品主记录，对应数据库 patterns 表。
 * 从 AI 生成结果创建，支持二次编辑，采用软删除（deleted_at）。
 */
@Entity
@Table(name = "patterns")
public class Pattern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pattern_id")
    private Integer patternId;

    /** 所有者用户ID */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** 作品名称 */
    @Column(nullable = false, length = 200)
    private String name;

    /** 作品描述 */
    @Lob
    @Column
    private String description;

    /** 作品高清图地址 */
    @Lob
    @Column(name = "image_url")
    private String imageUrl;

    /** 缩略图地址 */
    @Lob
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    /** 来源生成结果ID（占位引用，无硬外键） */
    @Column(name = "generation_result_id")
    private Integer generationResultId;

    /** 生成任务ID（字符串格式） */
    @Column(name = "task_id", length = 100)
    private String taskId;

    /** 生成参数：量化级别 2/3/4 */
    @Column(name = "k_value")
    private Integer kValue;

    /** 生成参数：噪声强度 0.0-1.0 */
    @Column(name = "noise_level")
    private Double noiseLevel;

    /** 生成参数：分块数 1/2/4/8 */
    @Column(name = "patch_mode")
    private Integer patchMode;

    /** 源草图ID */
    @Column(name = "sketch_id")
    private Integer sketchId;

    /** 风格参考图案ID */
    @Column(name = "style_ref_id")
    private Integer styleRefId;

    /** 标签，逗号分隔存储 */
    @Column(length = 500)
    private String tags;

    /** 是否公开 */
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = false;

    /** 纹样分类（如：云纹、螺旋纹） */
    @Column(length = 100)
    private String category;

    /** 浏览量（冗余计数） */
    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    /** 点赞数（冗余计数） */
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    /** 收藏数（冗余计数） */
    @Column(name = "favorite_count", nullable = false)
    private Integer favoriteCount = 0;

    /** 评论数（冗余计数） */
    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;

    /** 平均评分（来自生成结果评价） */
    @Column(name = "avg_rating")
    private Double avgRating;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 软删除标记 */
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

    // ========== Getters and Setters ==========

    public Integer getPatternId() { return patternId; }
    public void setPatternId(Integer patternId) { this.patternId = patternId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Integer getGenerationResultId() { return generationResultId; }
    public void setGenerationResultId(Integer generationResultId) { this.generationResultId = generationResultId; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public Integer getKValue() { return kValue; }
    public void setKValue(Integer kValue) { this.kValue = kValue; }

    public Double getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(Double noiseLevel) { this.noiseLevel = noiseLevel; }

    public Integer getPatchMode() { return patchMode; }
    public void setPatchMode(Integer patchMode) { this.patchMode = patchMode; }

    public Integer getSketchId() { return sketchId; }
    public void setSketchId(Integer sketchId) { this.sketchId = sketchId; }

    public Integer getStyleRefId() { return styleRefId; }
    public void setStyleRefId(Integer styleRefId) { this.styleRefId = styleRefId; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(Integer favoriteCount) { this.favoriteCount = favoriteCount; }

    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    public Double getAvgRating() { return avgRating; }
    public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}