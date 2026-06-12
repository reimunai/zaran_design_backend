package com.example.zaran_design_backend.dto.generation;

import com.example.zaran_design_backend.entity.GenerationTask;

import java.time.LocalDateTime;

/** 任务详情与进度响应 */
public class GenerationTaskDetailResponse {

    private String taskId;
    private Long sketchId;
    private GenerationParams params;
    private String status;
    private Integer progress;
    private String stage;
    private Integer queuePosition;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime estimatedCompleteAt;

    public static GenerationTaskDetailResponse of(GenerationTask task) {
        GenerationTaskDetailResponse response = new GenerationTaskDetailResponse();
        response.taskId = task.getTaskId();
        response.sketchId = task.getSketchId();
        response.params = new GenerationParams(task.getkValue(), task.getNoiseLevel(), 
                task.getPatchMode(), task.getStyleRefId());
        response.status = task.getStatus();
        response.progress = task.getProgress();
        response.stage = task.getStage();
        response.queuePosition = task.getQueuePosition();
        response.priority = task.getPriority();
        response.createdAt = task.getCreatedAt();
        response.startedAt = task.getStartedAt();
        response.estimatedCompleteAt = task.getEstimatedCompleteAt();
        return response;
    }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public Long getSketchId() { return sketchId; }
    public void setSketchId(Long sketchId) { this.sketchId = sketchId; }

    public GenerationParams getParams() { return params; }
    public void setParams(GenerationParams params) { this.params = params; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getProgress() { return progress; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public Integer getQueuePosition() { return queuePosition; }
    public void setQueuePosition(Integer queuePosition) { this.queuePosition = queuePosition; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getEstimatedCompleteAt() { return estimatedCompleteAt; }
    public void setEstimatedCompleteAt(LocalDateTime estimatedCompleteAt) { this.estimatedCompleteAt = estimatedCompleteAt; }
}