package com.example.zaran_design_backend.dto.generation;

import com.example.zaran_design_backend.entity.GenerationResult;

import java.time.LocalDateTime;

/** 生成结果响应 */
public class GenerationResultResponse {

    private Long resultId;
    private String taskId;
    private String imageUrl;
    private String thumbnailUrl;
    private GenerationParams params;
    private Float generationTime;
    private LocalDateTime createdAt;

    public static GenerationResultResponse of(GenerationResult result) {
        GenerationResultResponse response = new GenerationResultResponse();
        response.resultId = result.getResultId();
        response.taskId = result.getTaskId();
        response.imageUrl = result.getImageUrl();
        response.thumbnailUrl = result.getThumbnailUrl();
        response.params = new GenerationParams(result.getkValue(), result.getNoiseLevel(), 
                result.getPatchMode(), null);
        response.generationTime = result.getGenerationTime();
        response.createdAt = result.getCreatedAt();
        return response;
    }

    public Long getResultId() { return resultId; }
    public void setResultId(Long resultId) { this.resultId = resultId; }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public GenerationParams getParams() { return params; }
    public void setParams(GenerationParams params) { this.params = params; }

    public Float getGenerationTime() { return generationTime; }
    public void setGenerationTime(Float generationTime) { this.generationTime = generationTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}