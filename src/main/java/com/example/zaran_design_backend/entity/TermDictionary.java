package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "term_dictionary")
public class TermDictionary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_id")
    private Integer termId;

    @Column(name = "term_name", nullable = false, unique = true, length = 100)
    private String termName;

    @Column(length = 100)
    private String pinyin;

    @Column(length = 200)
    private String english;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String definition;


    @Column(name = "example_image", length = 255)
    private String exampleImage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // ========== Getter 和 Setter ==========
    public Integer getTermId() { return termId; }
    public void setTermId(Integer termId) { this.termId = termId; }

    public String getTermName() { return termName; }
    public void setTermName(String termName) { this.termName = termName; }

    public String getPinyin() { return pinyin; }
    public void setPinyin(String pinyin) { this.pinyin = pinyin; }

    public String getEnglish() { return english; }
    public void setEnglish(String english) { this.english = english; }

    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }

    public String getExampleImage() { return exampleImage; }
    public void setExampleImage(String exampleImage) { this.exampleImage = exampleImage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
