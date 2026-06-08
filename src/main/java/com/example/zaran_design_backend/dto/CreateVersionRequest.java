package com.example.zaran_design_backend.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 创建版本快照请求 */
public class CreateVersionRequest {

    @Size(max = 255, message = "版本描述最多255字")
    private String changeDesc;

    @NotNull(message = "图层快照不能为空")
    private JsonNode layersJson;

    private String imageBase64;

    public String getChangeDesc() { return changeDesc; }
    public void setChangeDesc(String changeDesc) { this.changeDesc = changeDesc; }

    public JsonNode getLayersJson() { return layersJson; }
    public void setLayersJson(JsonNode layersJson) { this.layersJson = layersJson; }

    public String getImageBase64() { return imageBase64; }
    public void setImageBase64(String imageBase64) { this.imageBase64 = imageBase64; }
}
