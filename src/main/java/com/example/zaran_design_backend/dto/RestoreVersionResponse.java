package com.example.zaran_design_backend.dto;

import com.fasterxml.jackson.databind.JsonNode;

/** 回退到指定版本响应 */
public class RestoreVersionResponse {

    private Integer sketchId;
    private Integer currentVersion;
    private Integer restoredFrom;
    private JsonNode layersJson;

    public Integer getSketchId() { return sketchId; }
    public void setSketchId(Integer sketchId) { this.sketchId = sketchId; }
    public Integer getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(Integer currentVersion) { this.currentVersion = currentVersion; }
    public Integer getRestoredFrom() { return restoredFrom; }
    public void setRestoredFrom(Integer restoredFrom) { this.restoredFrom = restoredFrom; }
    public JsonNode getLayersJson() { return layersJson; }
    public void setLayersJson(JsonNode layersJson) { this.layersJson = layersJson; }
}
