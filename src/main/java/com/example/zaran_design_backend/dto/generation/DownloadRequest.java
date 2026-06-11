package com.example.zaran_design_backend.dto.generation;

/** 下载请求 */
public class DownloadRequest {

    private String format = "png";
    private Boolean transparent = true;
    private Integer resolution;

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public Boolean getTransparent() { return transparent; }
    public void setTransparent(Boolean transparent) { this.transparent = transparent; }

    public Integer getResolution() { return resolution; }
    public void setResolution(Integer resolution) { this.resolution = resolution; }
}