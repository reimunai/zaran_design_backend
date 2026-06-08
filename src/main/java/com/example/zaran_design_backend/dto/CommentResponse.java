package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.PatternComment;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论响应（含子回复）
 */
public class CommentResponse {

    private Integer commentId;
    private String content;
    private PatternDetailResponse.AuthorBrief author;
    private Integer parentId;
    private Boolean isCulturalReview;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private List<CommentResponse> replies;  // 子回复列表

    public static CommentResponse of(PatternComment comment,
                                     PatternDetailResponse.AuthorBrief author,
                                     List<CommentResponse> replies) {
        CommentResponse r = new CommentResponse();
        r.commentId = comment.getCommentId();
        r.content = comment.getContent();
        r.author = author;
        r.parentId = comment.getParentId();
        r.isCulturalReview = comment.getIsCulturalReview();
        r.likeCount = comment.getLikeCount();
        r.createdAt = comment.getCreatedAt();
        r.replies = replies;
        return r;
    }

    public Integer getCommentId() { return commentId; }
    public void setCommentId(Integer commentId) { this.commentId = commentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public PatternDetailResponse.AuthorBrief getAuthor() { return author; }
    public void setAuthor(PatternDetailResponse.AuthorBrief author) { this.author = author; }
    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
    public Boolean getIsCulturalReview() { return isCulturalReview; }
    public void setIsCulturalReview(Boolean culturalReview) { isCulturalReview = culturalReview; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<CommentResponse> getReplies() { return replies; }
    public void setReplies(List<CommentResponse> replies) { this.replies = replies; }
}
