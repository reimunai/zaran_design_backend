package com.example.zaran_design_backend.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 6.3.4 编辑作品信息 — 请求参数
 */
public class UpdatePatternRequest {

    @Size(max = 200, message = "作品名称最多200字")
    private String name;

    @Size(max = 2000, message = "作品描述最多2000字")
    private String description;

    /** 标签数组 */
    private List<String> tags;

    /** 是否公开 */
    private Boolean isPublic;

    /** 纹样分类 */
    private String category;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
