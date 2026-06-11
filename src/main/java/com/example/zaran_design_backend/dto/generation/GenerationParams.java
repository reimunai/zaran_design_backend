package com.example.zaran_design_backend.dto.generation;

/** 生成参数 */
public class GenerationParams {

    private Integer kValue;
    private Float noiseLevel;
    private Integer patchMode;
    private Long styleRefId;

    public GenerationParams() {}

    public GenerationParams(Integer kValue, Float noiseLevel, Integer patchMode, Long styleRefId) {
        this.kValue = kValue;
        this.noiseLevel = noiseLevel;
        this.patchMode = patchMode;
        this.styleRefId = styleRefId;
    }

    public Integer getkValue() { return kValue; }
    public void setkValue(Integer kValue) { this.kValue = kValue; }

    public Float getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(Float noiseLevel) { this.noiseLevel = noiseLevel; }

    public Integer getPatchMode() { return patchMode; }
    public void setPatchMode(Integer patchMode) { this.patchMode = patchMode; }

    public Long getStyleRefId() { return styleRefId; }
    public void setStyleRefId(Long styleRefId) { this.styleRefId = styleRefId; }
}