package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 草图版本快照，对应数据库 sketch_versions 表。
 * 版本号 version_number 针对每张草图从 1 自增，UK(sketch_id, version_number)。
 */
@Entity
@Table(name = "sketch_versions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sketch_id", "version_number"})
})
public class SketchVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "version_id")
    private Integer versionId;

    @Column(name = "sketch_id", nullable = false)
    private Integer sketchId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "change_desc", length = 255)
    private String changeDesc;

    /** 该版本的图层快照（JSON 字符串） */
    @Lob
    @Column(name = "layers_json")
    private String layersJson;

    @Lob
    @Column(name = "thumbnail_path")
    private String thumbnailPath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Integer getVersionId() { return versionId; }
    public void setVersionId(Integer versionId) { this.versionId = versionId; }

    public Integer getSketchId() { return sketchId; }
    public void setSketchId(Integer sketchId) { this.sketchId = sketchId; }

    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }

    public String getChangeDesc() { return changeDesc; }
    public void setChangeDesc(String changeDesc) { this.changeDesc = changeDesc; }

    public String getLayersJson() { return layersJson; }
    public void setLayersJson(String layersJson) { this.layersJson = layersJson; }

    public String getThumbnailPath() { return thumbnailPath; }
    public void setThumbnailPath(String thumbnailPath) { this.thumbnailPath = thumbnailPath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
