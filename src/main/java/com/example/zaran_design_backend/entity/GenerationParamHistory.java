package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户常用参数历史实体，对应数据库 generation_param_history 表。
 */
@Entity
@Table(name = "generation_param_history")
public class GenerationParamHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    /** 所属用户ID */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** 量化级别 */
    @Column(name = "k_value", nullable = false)
    private Integer kValue;

    /** 噪声强度 */
    @Column(name = "noise_level", nullable = false)
    private Float noiseLevel;

    /** 分块数 */
    @Column(name = "patch_mode", nullable = false)
    private Integer patchMode;

    /** 使用次数 */
    @Column(name = "use_count", nullable = false)
    private Integer useCount = 1;

    /** 最后使用时间 */
    @Column(name = "last_used_at", nullable = false)
    private LocalDateTime lastUsedAt;

    @PrePersist
    protected void onCreate() {
        lastUsedAt = LocalDateTime.now();
    }

    public Long getHistoryId() { return historyId; }
    public void setHistoryId(Long historyId) { this.historyId = historyId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

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