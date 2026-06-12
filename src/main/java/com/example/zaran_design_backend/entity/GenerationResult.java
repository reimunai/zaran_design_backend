package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * AI生成结果实体，对应数据库 generation_results 表。
 */
@Entity
@Table(name = "generation_results")
public class GenerationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    /** 关联的任务ID */
    @Column(name = "task_id", nullable = false, length = 50)
    private String taskId;

    /** 生成图片URL */
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    /** 缩略图URL */
    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    /** 量化级别 */
    @Column(name = "k_value", nullable = false)
    private Integer kValue;

    /** 噪声强度 */
    @Column(name = "noise_level", nullable = false)
    private Float noiseLevel;

    /** 分块数 */
    @Column(name = "patch_mode", nullable = false)
    private Integer patchMode;

    /** 生成耗时（秒） */
    @Column(name = "generation_time")
    private Float generationTime;

    /** 用户评分 1-5 */
    @Column
    private Integer rating;

    /** 评价备注 */
    @Lob
    @Column
    private String comment;

    /** 是否收藏 */
    @Column(name = "is_favorite")
    private Boolean isFavorite = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getResultId() { return resultId; }
    public void setResultId(Long resultId) { this.resultId = resultId; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Integer getkValue() { return kValue; }
    public void setkValue(Integer kValue) { this.kValue = kValue; }

    public Float getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(Float noiseLevel) { this.noiseLevel = noiseLevel; }

    public Integer getPatchMode() { return patchMode; }
    public void setPatchMode(Integer patchMode) { this.patchMode = patchMode; }

    public Float getGenerationTime() { return generationTime; }
    public void setGenerationTime(Float generationTime) { this.generationTime = generationTime; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}