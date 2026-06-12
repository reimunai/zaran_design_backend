package com.example.zaran_design_backend.dto.generation;

/** 下载响应 */
public class DownloadResponse {

    private String downloadUrl;
    private Integer expiresIn;
    private String format;
    private Integer resolution;

    public DownloadResponse() {}

    public DownloadResponse(String downloadUrl, Integer expiresIn, String format, Integer resolution) {
        this.downloadUrl = downloadUrl;
        this.expiresIn = expiresIn;
        this.format = format;
        this.resolution = resolution;
    }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public Integer getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Integer expiresIn) { this.expiresIn = expiresIn; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public Integer getResolution() { return resolution; }
    public void setResolution(Integer resolution) { this.resolution = resolution; }
}