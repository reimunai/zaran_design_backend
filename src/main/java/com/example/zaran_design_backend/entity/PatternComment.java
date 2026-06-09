package com.example.zaran_design_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 作品评论，对应数据库 pattern_comments 表。
 * 支持楼中楼回复（parentId 指向父评论）。
 */
@Entity
@Table(name = "pattern_comments")
public class PatternComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    @Column(name = "pattern_id", nullable = false)
    private Integer patternId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** 评论内容，最多500字 */
    @Column(nullable = false, length = 500)
    private String content;

    /** 父评论ID（楼中楼回复），null 表示顶级评论 */
    @Column(name = "parent_id")
    private Integer parentId;

    /** 是否为文化评审标记 */
    @Column(name = "is_cultural_review", nullable = false)
    private Boolean isCulturalReview = false;

    /** 评论点赞数 */
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Integer getCommentId() { return commentId; }
    public void setCommentId(Integer commentId) { this.commentId = commentId; }

    public Integer getPatternId() { return patternId; }
    public void setPatternId(Integer patternId) { this.patternId = patternId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public Boolean getIsCulturalReview() { return isCulturalReview; }
    public void setIsCulturalReview(Boolean isCulturalReview) { this.isCulturalReview = isCulturalReview; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}