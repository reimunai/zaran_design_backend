package com.example.zaran_design_backend.dto;

public class UpdateProfileRequest {

    private String avatar;
    private String bio;
    private String professionField;
    private String portfolioUrl;

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getProfessionField() { return professionField; }
    public void setProfessionField(String professionField) { this.professionField = professionField; }
    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }
}
