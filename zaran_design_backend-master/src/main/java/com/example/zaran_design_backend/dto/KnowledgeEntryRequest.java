package com.example.zaran_design_backend.dto;

import java.util.List;

public class KnowledgeEntryRequest {
    private String title;
    private String content;
    private String category;
    private List<String> imageAttachments;

    // Getter 和 Setter
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<String> getImageAttachments() { return imageAttachments; }
    public void setImageAttachments(List<String> imageAttachments) { this.imageAttachments = imageAttachments; }
}