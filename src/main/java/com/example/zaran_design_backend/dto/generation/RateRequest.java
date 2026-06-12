package com.example.zaran_design_backend.dto.generation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** 评分请求 */
public class RateRequest {

    @NotNull(message = "rating不能为空")
    @Min(value = 1, message = "rating最小值为1")
    @Max(value = 5, message = "rating最大值为5")
    private Integer rating;

    private String comment;

    private Boolean isFavorite = false;

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Boolean getIsFavorite() { return isFavorite; }
    public void setIsFavorite(Boolean isFavorite) { this.isFavorite = isFavorite; }
}