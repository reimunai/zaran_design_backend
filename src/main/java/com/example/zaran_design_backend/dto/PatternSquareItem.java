package com.example.zaran_design_backend.dto;

import com.example.zaran_design_backend.entity.Pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 6.3.3 作品广场列表项
 */
public class PatternSquareItem {

    private Integer patternId;
    private String name;
    private String thumbnailUrl;
    private PatternDetailResponse.AuthorBrief author;
    private SquareStats stats;
    private List<String> tags;

    public static class SquareStats {
        private Integer viewCount;
        private Integer likeCount;
        private Integer favoriteCount;

        public Integer getViewCount() { return viewCount; }
        public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
        public Integer getLikeCount() { return likeCount; }
        public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
        public Integer getFavoriteCount() { return favoriteCount; }
        public void setFavoriteCount(Integer favoriteCount) { this.favoriteCount = favoriteCount; }
    }

    public static PatternSquareItem of(Pattern pattern, PatternDetailResponse.AuthorBrief author) {
        PatternSquareItem item = new PatternSquareItem();
        item.patternId = pattern.getPatternId();
        item.name = pattern.getName();
        item.thumbnailUrl = pattern.getThumbnailUrl();
        item.author = author;

        SquareStats s = new SquareStats();
        s.viewCount = pattern.getViewCount();
        s.likeCount = pattern.getLikeCount();
        s.favoriteCount = pattern.getFavoriteCount();
        item.stats = s;

        item.tags = splitTags(pattern.getTags());
        return item;
    }

    private static List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) return new ArrayList<>();
        return Arrays.stream(tags.split(","))
                .map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public Integer getPatternId() { return patternId; }
    public void setPatternId(Integer patternId) { this.patternId = patternId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }
    public PatternDetailResponse.AuthorBrief getAuthor() { return author; }
    public void setAuthor(PatternDetailResponse.AuthorBrief author) { this.author = author; }
    public SquareStats getStats() { return stats; }
    public void setStats(SquareStats stats) { this.stats = stats; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
