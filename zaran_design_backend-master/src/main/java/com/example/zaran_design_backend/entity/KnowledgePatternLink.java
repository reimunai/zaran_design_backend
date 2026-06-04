package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_pattern_links")
public class KnowledgePatternLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_id")
    private Integer linkId;

    @Column(name = "entry_id", nullable = false)
    private Integer entryId;

    @Column(name = "pattern_id", nullable = false)
    private Integer patternId;

    @Column(name = "relation_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RelationType relationType = RelationType.example;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum RelationType {
        inspiration, technique, example
    }

    // ========== Getter 和 Setter ==========
    public Integer getLinkId() { return linkId; }
    public void setLinkId(Integer linkId) { this.linkId = linkId; }

    public Integer getEntryId() { return entryId; }
    public void setEntryId(Integer entryId) { this.entryId = entryId; }

    public Integer getPatternId() { return patternId; }
    public void setPatternId(Integer patternId) { this.patternId = patternId; }

    public RelationType getRelationType() { return relationType; }
    public void setRelationType(RelationType relationType) { this.relationType = relationType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}