package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 协同编辑会话版本快照，对应数据库 collab_session_versions 表。
 * 支持手动保存版本和回滚操作。
 */
@Entity
@Table(name = "collab_session_versions")
public class CollabSessionVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_id")
    private Integer versionId;

    @Column(name = "session_id", nullable = false)
    private Integer sessionId;

    /** 保存版本的操作用户ID */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** 版本号（从1递增） */
    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    /** 该版本的完整图层数据快照（JSON） */
    @Lob
    @Column(name = "layers_json")
    private String layersJson;

    /** 版本描述 */
    @Column(name = "change_desc", length = 255)
    private String changeDesc;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Integer getVersionId() { return versionId; }
    public void setVersionId(Integer versionId) { this.versionId = versionId; }

    public Integer getSessionId() { return sessionId; }
    public void setSessionId(Integer sessionId) { this.sessionId = sessionId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }

    public String getLayersJson() { return layersJson; }
    public void setLayersJson(String layersJson) { this.layersJson = layersJson; }

    public String getChangeDesc() { return changeDesc; }
    public void setChangeDesc(String changeDesc) { this.changeDesc = changeDesc; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
