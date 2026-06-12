package com.example.zaran_design_backend.dto.generation;

import jakarta.validation.constraints.*;

/** 创建生成任务请求 */
public class CreateGenerationTaskRequest {

    @NotNull(message = "sketchId不能为空")
    private Long sketchId;

    @NotNull(message = "kValue不能为空")
    @Min(value = 2, message = "kValue最小值为2")
    @Max(value = 4, message = "kValue最大值为4")
    private Integer kValue;

    @NotNull(message = "noiseLevel不能为空")
    @DecimalMin(value = "0.0", message = "noiseLevel最小值为0.0")
    @DecimalMax(value = "1.0", message = "noiseLevel最大值为1.0")
    private Float noiseLevel;

    @NotNull(message = "patchMode不能为空")
    @Min(value = 1, message = "patchMode最小值为1")
    @Max(value = 8, message = "patchMode最大值为8")
    private Integer patchMode;

    private Long styleRefId;

    @Min(value = 1, message = "priority最小值为1")
    @Max(value = 10, message = "priority最大值为10")
    private Integer priority = 5;

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
}