package com.example.zaran_design_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 6.3.5 发表评论 — 请求参数
 */
public class CreateCommentRequest {

    /** 评论内容，最多500字 */
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容最多500字")
    private String content;

    /** 父评论ID（楼中楼回复），null 表示顶级评论 */
    private Long parentId;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}
