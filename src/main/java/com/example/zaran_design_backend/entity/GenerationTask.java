package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * AI生成任务实体，对应数据库 generation_tasks 表。
 */
@Entity
@Table(name = "generation_tasks")
public class GenerationTask {

    @Id
    @Column(name = "task_id", length = 50)
    private String taskId;

    /** 所属用户ID */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** 源草图ID */
    @Column(name = "sketch_id", nullable = false)
    private Long sketchId;

    /** 量化级别：2/3/4（二道/三道/四道浸染） */
    @Column(name = "k_value", nullable = false)
    private Integer kValue;

    /** 噪声强度，范围 0.0-1.0 */
    @Column(name = "noise_level", nullable = false)
    private Float noiseLevel;

    /** 分块数：1/2/4/8 */
    @Column(name = "patch_mode", nullable = false)
    private Integer patchMode;

    /** 风格参考图案ID */
    @Column(name = "style_ref_id")
    private Long styleRefId;

    /** 优先级 1-10，默认5 */
    @Column(nullable = false)
    private Integer priority = 5;

    /** 任务状态：queued/processing/completed/failed/canceled */
    @Column(nullable = false, length = 20)
    private String status;

    /** 进度百分比 0-100 */
    @Column(nullable = false)
    private Integer progress = 0;

    /** 当前阶段：queued/model_inference/post_processing/complete */
    @Column(length = 50)
    private String stage;

    /** 队列位置 */
    @Column(name = "queue_position")
    private Integer queuePosition;

    /** 错误信息 */
    @Lob
    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "estimated_complete_at")
    private LocalDateTime estimatedCompleteAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = "queued";
        }
    }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Long getSketchId() { return sketchId; }
    public void setSketchId(Long sketchId) { this.sketchId = sketchId; }

    public Integer getkValue() { return kValue; }
    public void setkValue(Integer kValue) { this.kValue = kValue; }

    public Float getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(Float noiseLevel) { this.noiseLevel = noiseLevel; }

    public Integer getPatchMode() { return patchMode; }
    public void setPatchMode(Integer patchMode) { this.patchMode = patchMode; }

    public Long getStyleRefId() { return styleRefId; }
    public void setStyleRefId(Long styleRefId) { this.styleRefId = styleRefId; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public Integer getQueuePosition() { return queuePosition; }
    public void setQueuePosition(Integer queuePosition) { this.queuePosition = queuePosition; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getEstimatedCompleteAt() { return estimatedCompleteAt; }
    public void setEstimatedCompleteAt(LocalDateTime estimatedCompleteAt) { this.estimatedCompleteAt = estimatedCompleteAt; }
}