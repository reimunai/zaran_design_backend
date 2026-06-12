package com.example.zaran_design_backend.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.Size;

/**
 * 手动保存版本请求（7.2.8）
 */
public class SaveVersionRequest {

    @Size(max = 255, message = "版本描述最多255字")
    private String changeDesc;

    private JsonNode layersJson;

    public String getChangeDesc() { return changeDesc; }
    public void setChangeDesc(String changeDesc) { this.changeDesc = changeDesc; }

    public JsonNode getLayersJson() { return layersJson; }
    public void setLayersJson(JsonNode layersJson) { this.layersJson = layersJson; }
}
