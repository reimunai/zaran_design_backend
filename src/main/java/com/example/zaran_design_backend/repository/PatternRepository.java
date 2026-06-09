package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.Pattern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PatternRepository extends JpaRepository<Pattern, Integer>,
        JpaSpecificationExecutor<Pattern> {

    /** 按ID查询未删除的作品 */
    Optional<Pattern> findByPatternIdAndDeletedAtIsNull(Integer patternId);

    /** 查询用户未删除的作品（分页） */
    Page<Pattern> findByUserIdAndDeletedAtIsNull(Integer userId, Pageable pageable);

    /** 查询所有未删除的公开作品（分页） */
    Page<Pattern> findByIsPublicTrueAndDeletedAtIsNull(Pageable pageable);

    /** 按用户ID统计作品数 */
    long countByUserIdAndDeletedAtIsNull(Integer userId);

    /** 按用户ID查询公开作品 */
    Page<Pattern> findByUserIdAndIsPublicTrueAndDeletedAtIsNull(Integer userId, Pageable pageable);

    /** 按标签模糊搜索公开作品 */
    @Query("SELECT p FROM Pattern p WHERE p.isPublic = true AND p.deletedAt IS NULL " +
           "AND (p.name LIKE %:keyword% OR p.tags LIKE %:keyword%)")
    Page<Pattern> searchPublic(@Param("keyword") String keyword, Pageable pageable);

    /** 热门排序：按点赞数+收藏数+浏览量加权 */
    @Query("SELECT p FROM Pattern p WHERE p.isPublic = true AND p.deletedAt IS NULL " +
           "ORDER BY (p.likeCount * 2 + p.favoriteCount * 3 + p.viewCount * 0.1) DESC")
    Page<Pattern> findPublicByHot(Pageable pageable);

    /** 最新排序 */
    @Query("SELECT p FROM Pattern p WHERE p.isPublic = true AND p.deletedAt IS NULL " +
           "ORDER BY p.createdAt DESC")
    Page<Pattern> findPublicByNewest(Pageable pageable);

    /** 按收藏数排序 */
    @Query("SELECT p FROM Pattern p WHERE p.isPublic = true AND p.deletedAt IS NULL " +
           "ORDER BY p.favoriteCount DESC")
    Page<Pattern> findPublicByFavorite(Pageable pageable);
}
