package com.example.zaran_design_backend.dto;

import jakarta.validation.constraints.Size;

/**
 * 6.3.5 二次编辑图案 — 请求参数
 */
public class EditPatternRequest {

    /** 编辑描述 */
    @Size(max = 500, message = "编辑描述最多500字")
    private String editDesc;

    /** 编辑后的图片地址（base64 data URL 或 MinIO 地址） */
    private String imageBase64;

    /** 编辑后的缩略图 */
    private String thumbnailBase64;

    public String getEditDesc() { return editDesc; }
    public void setEditDesc(String editDesc) { this.editDesc = editDesc; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }

    public String getThumbnailBase64() { return thumbnailBase64; }
    public void setThumbnailBase64(String thumbnailBase64) { this.thumbnailBase64 = thumbnailBase64; }
}
