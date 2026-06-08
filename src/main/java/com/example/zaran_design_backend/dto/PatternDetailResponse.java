package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.Pattern;
import com.example.zaran_design_backend.entity.PatternEdit;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 6.3.2 作品详情 — 响应
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatternDetailResponse {

    private Integer patternId;
    private String name;
    private String imageUrl;
    private String thumbnailUrl;
    private String description;
    private AuthorBrief author;
    private GenerationInfo generationInfo;
    private List<VersionNode> versionTree;
    private Stats stats;
    private List<String> tags;
    private String category;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private Boolean isOwner;
    private Boolean isLiked;
    private Boolean isFavorited;

    // ========== 内嵌类型 ==========

    public static class AuthorBrief {
        private Integer userId;
        private String username;
        private String avatar;
        private String role;

        public AuthorBrief() {}
        public AuthorBrief(Integer userId, String username, String avatar, String role) {
            this.userId = userId;
            this.username = username;
            this.avatar = avatar;
            this.role = role;
        }

        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }

    public static class GenerationInfo {
        private String taskId;
        private Integer kValue;
        private Double noiseLevel;
        private Integer patchMode;
        private Integer sketchId;

        public GenerationInfo() {}
        public GenerationInfo(String taskId, Integer kValue, Double noiseLevel,
                             Integer patchMode, Integer sketchId) {
            this.taskId = taskId;
            this.kValue = kValue;
            this.noiseLevel = noiseLevel;
            this.patchMode = patchMode;
            this.sketchId = sketchId;
        }

        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public Integer getKValue() { return kValue; }
        public void setKValue(Integer kValue) { this.kValue = kValue; }
        public Double getNoiseLevel() { return noiseLevel; }
        public void setNoiseLevel(Double noiseLevel) { this.noiseLevel = noiseLevel; }
        public Integer getPatchMode() { return patchMode; }
        public void setPatchMode(Integer patchMode) { this.patchMode = patchMode; }
        public Integer getSketchId() { return sketchId; }
        public void setSketchId(Integer sketchId) { this.sketchId = sketchId; }
    }

    public static class VersionNode {
        private Integer version;
        private String type;       // sketch / ai_generation / manual_edit
        private Integer sketchId;
        private String taskId;
        private String editDesc;
        private LocalDateTime createdAt;

        public Integer getVersion() { return version; }
        public void setVersion(Integer version) { this.version = version; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Integer getSketchId() { return sketchId; }
        public void setSketchId(Integer sketchId) { this.sketchId = sketchId; }
        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }
        public String getEditDesc() { return editDesc; }
        public void setEditDesc(String editDesc) { this.editDesc = editDesc; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public static class Stats {
        private Integer viewCount;
        private Integer likeCount;
        private Integer favoriteCount;
        private Integer commentCount;
        private Double avgRating;

        public Integer getViewCount() { return viewCount; }
        public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
        public Integer getLikeCount() { return likeCount; }
        public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
        public Integer getFavoriteCount() { return favoriteCount; }
        public void setFavoriteCount(Integer favoriteCount) { this.favoriteCount = favoriteCount; }
        public Integer getCommentCount() { return commentCount; }
        public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
        public Double getAvgRating() { return avgRating; }
        public void setAvgRating(Double avgRating) { this.avgRating = avgRating; }
    }

    // ========== 工厂方法 ==========

    /** 从 Pattern 实体构建详情响应 */
    public static PatternDetailResponse of(Pattern pattern, AuthorBrief author,
                                           boolean isOwner, Boolean isLiked, Boolean isFavorited) {
        PatternDetailResponse r = new PatternDetailResponse();
        r.patternId = pattern.getPatternId();
        r.name = pattern.getName();
        r.imageUrl = pattern.getImageUrl();
        r.thumbnailUrl = pattern.getThumbnailUrl();
        r.description = pattern.getDescription();
        r.author = author;
        r.category = pattern.getCategory();
        r.isPublic = pattern.getIsPublic();
        r.createdAt = pattern.getCreatedAt();
        r.isOwner = isOwner;
        r.isLiked = isLiked;
        r.isFavorited = isFavorited;

        // Generation info
        if (pattern.getTaskId() != null || pattern.getKValue() != null) {
            r.generationInfo = new GenerationInfo(
                    pattern.getTaskId(), pattern.getKValue(),
                    pattern.getNoiseLevel(), pattern.getPatchMode(),
                    pattern.getSketchId());
        }

        // Stats
        Stats stats = new Stats();
        stats.viewCount = pattern.getViewCount();
        stats.likeCount = pattern.getLikeCount();
        stats.favoriteCount = pattern.getFavoriteCount();
        stats.commentCount = pattern.getCommentCount();
        stats.avgRating = pattern.getAvgRating();
        r.stats = stats;

        // Tags
        r.tags = splitTags(pattern.getTags());
        return r;
    }

    /** 构建 versionTree */
    public void buildVersionTree(Pattern pattern, List<PatternEdit> edits) {
        List<VersionNode> tree = new ArrayList<>();
        int v = 0;

        // 1. 草图版本
        if (pattern.getSketchId() != null) {
            VersionNode node = new VersionNode();
            node.setVersion(++v);
            node.setType("sketch");
            node.setSketchId(pattern.getSketchId());
            node.setCreatedAt(pattern.getCreatedAt());
            tree.add(node);
        }

        // 2. AI生成版本
        if (pattern.getTaskId() != null) {
            VersionNode node = new VersionNode();
            node.setVersion(++v);
            node.setType("ai_generation");
            node.setTaskId(pattern.getTaskId());
            node.setCreatedAt(pattern.getCreatedAt());
            tree.add(node);
        }

        // 3. 手动编辑版本
        if (edits != null) {
            for (PatternEdit edit : edits) {
                VersionNode node = new VersionNode();
                node.setVersion(++v);
                node.setType("manual_edit");
                node.setEditDesc(edit.getEditDesc());
                node.setCreatedAt(edit.getCreatedAt());
                tree.add(node);
            }
        }

        this.versionTree = tree;
    }

    private static List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) return new ArrayList<>();
        return Arrays.stream(tags.split(","))
                .map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    // ========== Getters and Setters ==========

    public Integer getPatternId() { return patternId; }
    public void setPatternId(Integer patternId) { this.patternId = patternId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public AuthorBrief getAuthor() { return author; }
    public void setAuthor(AuthorBrief author) { this.author = author; }
    public GenerationInfo getGenerationInfo() { return generationInfo; }
    public void setGenerationInfo(GenerationInfo generationInfo) { this.generationInfo = generationInfo; }
    public List<VersionNode> getVersionTree() { return versionTree; }
    public void setVersionTree(List<VersionNode> versionTree) { this.versionTree = versionTree; }
    public Stats getStats() { return stats; }
    public void setStats(Stats stats) { this.stats = stats; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Boolean getIsOwner() { return isOwner; }
    public void setIsOwner(Boolean isOwner) { this.isOwner = isOwner; }
    public Boolean getIsLiked() { return isLiked; }
    public void setLiked(Boolean liked) { isLiked = liked; }
    public Boolean getIsFavorited() { return isFavorited; }
    public void setFavorited(Boolean favorited) { isFavorited = favorited; }
}
