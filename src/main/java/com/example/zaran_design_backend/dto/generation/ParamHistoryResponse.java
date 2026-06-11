package com.example.zaran_design_backend.dto.generation;

import com.example.zaran_design_backend.entity.GenerationParamHistory;

import java.time.LocalDateTime;

/** 常用参数响应 */
public class ParamHistoryResponse {

    private Integer kValue;
    private Float noiseLevel;
    private Integer patchMode;
    private Integer useCount;
    private LocalDateTime lastUsedAt;

    public static ParamHistoryResponse of(GenerationParamHistory history) {
        ParamHistoryResponse response = new ParamHistoryResponse();
        response.kValue = history.getkValue();
        response.noiseLevel = history.getNoiseLevel();
        response.patchMode = history.getPatchMode();
        response.useCount = history.getUseCount();
        response.lastUsedAt = history.getLastUsedAt();
        return response;
    }

    public Integer getkValue() { return kValue; }
    public void setkValue(Integer kValue) { this.kValue = kValue; }

    public Float getNoiseLevel() { return noiseLevel; }
    public void setNoiseLevel(Float noiseLevel) { this.noiseLevel = noiseLevel; }

    public Integer getPatchMode() { return patchMode; }
    public void setPatchMode(Integer patchMode) { this.patchMode = patchMode; }

    public Integer getUseCount() { return useCount; }
    public void setUseCount(Integer useCount) { this.useCount = useCount; }

    public LocalDateTime getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(LocalDateTime lastUsedAt) { this.lastUsedAt = lastUsedAt; }
}