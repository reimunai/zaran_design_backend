package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 作品手动编辑记录，对应数据库 pattern_edits 表。
 * 用于构建 versionTree 中的 manual_edit 节点。
 */
@Entity
@Table(name = "pattern_edits")
public class PatternEdit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "edit_id")
    private Integer editId;

    @Column(name = "pattern_id", nullable = false)
    private Integer patternId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** 编辑描述 */
    @Column(name = "edit_desc", length = 500)
    private String editDesc;

    /** 编辑后的图片地址 */
    @Lob
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Integer getEditId() { return editId; }
    public void setEditId(Integer editId) { this.editId = editId; }

    public Integer getPatternId() { return patternId; }
    public void setPatternId(Integer patternId) { this.patternId = patternId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getEditDesc() { return editDesc; }
    public void setEditDesc(String editDesc) { this.editDesc = editDesc; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
