package com.example.zaran_design_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 6.3.1 从生成结果创建作品 — 请求参数
 */
public class CreatePatternRequest {

    /** 生成结果ID */
    @NotNull(message = "generationResultId 不能为空")
    private Long generationResultId;

    /** 作品名称 */
    @NotBlank(message = "作品名称不能为空")
    @Size(max = 200, message = "作品名称最多200字")
    private String name;

    /** 作品描述 */
    @Size(max = 2000, message = "作品描述最多2000字")
    private String description;

    /** 标签数组 */
    private List<String> tags;

    /** 生成任务ID */
    private String taskId;

    /** 生成参数 */
    private Integer kValue;
    private Double noiseLevel;
    private Integer patchMode;

    /** 源草图ID */
    private Integer sketchId;

    /** 作品图片地址（来自生成结果） */
    private String imageUrl;

    /** 缩略图地址 */
    private String thumbnailUrl;

    /** 是否公开，默认false */
    private Boolean isPublic = false;

    /** 纹样分类 */
    private String category;

    public Long getGenerationResultId() { return generationResultId; }
    public void setGenerationResultId(Long generationResultId) { this.generationResultId = generationResultId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

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

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
