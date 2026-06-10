package com.example.zaran_design_backend.dto;

/**
 * 禁用/启用用户请求。
 */
public class UpdateUserStatusRequest {

    private Integer status;

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
