package com.example.zaran_design_backend.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** 保存草图请求（全量更新图层数据与基本信息） */
public class SaveSketchRequest {

    private String name;

    /** 图层结构数据（矢量路径、栅格图层等） */
    @NotNull(message = "图层数据不能为空")
    private JsonNode layersJson;

    /** 预览图Base64，用于生成缩略图；自动保存时可省略 */
    private String imageBase64;

    private Integer categoryId;

    /** 是否为前端定时自动保存，默认false */
    private Boolean isAutoSave = false;

    private List<String> tags;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public JsonNode getLayersJson() { return layersJson; }
    public void setLayersJson(JsonNode layersJson) { this.layersJson = layersJson; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public Boolean getIsAutoSave() { return isAutoSave; }
    public void setIsAutoSave(Boolean isAutoSave) { this.isAutoSave = isAutoSave; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
