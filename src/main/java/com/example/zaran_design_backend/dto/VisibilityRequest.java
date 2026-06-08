package com.example.zaran_design_backend.dto;

import jakarta.validation.constraints.NotNull;

/** 设置草图公开/私密请求 */
public class VisibilityRequest {

    @NotNull(message = "isPublic不能为空")
    private Boolean isPublic;

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
}
