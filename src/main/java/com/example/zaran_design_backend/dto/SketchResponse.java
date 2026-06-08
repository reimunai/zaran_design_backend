package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.Sketch;

import java.time.LocalDateTime;

/** 创建草图响应 */
public class SketchResponse {

    private Integer sketchId;
    private String name;
    private Integer width;
    private Integer height;
    private Integer categoryId;
    private Boolean isPublic;
    private LocalDateTime createdAt;

    public static SketchResponse of(Sketch sketch) {
        SketchResponse response = new SketchResponse();
        response.sketchId = sketch.getSketchId();
        response.name = sketch.getName();
        response.width = sketch.getWidth();
        response.height = sketch.getHeight();
        response.categoryId = sketch.getCategoryId();
        response.isPublic = sketch.getIsPublic();
        response.createdAt = sketch.getCreatedAt();
        return response;
    }

    public Integer getSketchId() { return sketchId; }
    public void setSketchId(Integer sketchId) { this.sketchId = sketchId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
