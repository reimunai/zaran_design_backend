package com.example.zaran_design_backend.dto;

/** 导入本地图片响应 */
public class ImportImageResponse {

    private String layerId;
    private String imageUrl;
    private Integer width;
    private Integer height;

    public ImportImageResponse() {
    }

    public ImportImageResponse(String layerId, String imageUrl, Integer width, Integer height) {
        this.layerId = layerId;
        this.imageUrl = imageUrl;
        this.width = width;
        this.height = height;
    }

    public String getLayerId() { return layerId; }
    public void setLayerId(String layerId) { this.layerId = layerId; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
}
