package com.example.zaran_design_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** 创建新草图请求 */
public class CreateSketchRequest {

    /** 草图名称，缺省为「未命名草图」 */
    private String name;

    @NotNull(message = "画布宽度不能为空")
    @Min(value = 512, message = "画布宽度范围为512-4096")
    @Max(value = 4096, message = "画布宽度范围为512-4096")
    private Integer width;

    @NotNull(message = "画布高度不能为空")
    @Min(value = 512, message = "画布高度范围为512-4096")
    @Max(value = 4096, message = "画布高度范围为512-4096")
    private Integer height;

    private Integer categoryId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
}
