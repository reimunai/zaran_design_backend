package com.example.zaran_design_backend.controller;

import com.example.zaran_design_backend.common.Result;
import com.example.zaran_design_backend.dto.*;
import com.example.zaran_design_backend.entity.Pattern;
import com.example.zaran_design_backend.service.PatternService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 图案作品模块（第6模块）。
 *
 * <p>个人作品管理 + 作品广场（瀑布流/搜索/点赞/收藏/评论/举报）。</p>
 */
@RestController
@RequestMapping("/api/patterns")
public class PatternController {

    private final PatternService patternService;

    public PatternController(PatternService patternService) {
        this.patternService = patternService;
    }

    // ========================================================================
    // 6.1 个人作品管理
    // ========================================================================

    /**
     * 6.3.1 从生成结果创建作品
     * POST /api/patterns
     */
    @PostMapping
    public Result<PatternDetailResponse> createPattern(@Valid @RequestBody CreatePatternRequest request) {
        Integer userId = currentUserId();
        PatternDetailResponse response = patternService.createPattern(userId, request);
        return Result.ok("作品创建成功", response);
    }

    /**
     * 6.3.2 我的作品列表
     * GET /api/patterns/my
     */
    @GetMapping("/my")
    public Result<PageResponse<PatternSquareItem>> listMyPatterns(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "newest") String sort) {
        Integer userId = currentUserId();
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 1);
        return Result.ok(patternService.listMyPatterns(userId, safePage, safeSize, keyword, sort));
    }

    /**
     * 6.3.3 作品详情（可选认证）
     * GET /api/patterns/{patternId}
     * 未登录用户也可以查看公开作品（但不返回 isOwner/isLiked/isFavorited 等个性化信息）
     */
    @GetMapping("/{patternId}")
    public Result<PatternDetailResponse> getPatternDetail(@PathVariable Integer patternId) {
        Integer userId = currentUserIdOptional();
        return Result.ok(patternService.getPatternDetail(patternId, userId));
    }

    /**
     * 6.3.4 编辑作品信息
     * PUT /api/patterns/{patternId}
     */
    @PutMapping("/{patternId}")
    public Result<PatternDetailResponse> updatePattern(@PathVariable Integer patternId,
                                                       @Valid @RequestBody UpdatePatternRequest request) {
        Integer userId = currentUserId();
        return Result.ok("作品信息已更新", patternService.updatePattern(patternId, userId, request));
    }

    /**
     * 6.3.5 二次编辑图案
     * POST /api/patterns/{patternId}/edit
     */
    @PostMapping("/{patternId}/edit")
    public Result<PatternDetailResponse> editPattern(@PathVariable Integer patternId,
                                                     @Valid @RequestBody EditPatternRequest request) {
        Integer userId = currentUserId();
        return Result.ok("编辑已保存", patternService.editPattern(patternId, userId, request));
    }

    /**
     * 6.3.6 删除作品（软删除）
     * DELETE /api/patterns/{patternId}
     */
    @DeleteMapping("/{patternId}")
    public Result<Map<String, Object>> deletePattern(@PathVariable Integer patternId) {
        Integer userId = currentUserId();
        var deletedAt = patternService.softDeletePattern(patternId, userId);
        Map<String, Object> data = Map.of(
                "patternId", patternId,
                "deletedAt", deletedAt);
        return Result.ok("已移入回收站", data);
    }

    /**
     * 6.3.7 恢复已删除作品
     * POST /api/patterns/{patternId}/recover
     */
    @PostMapping("/{patternId}/recover")
    public Result<Map<String, Object>> recoverPattern(@PathVariable Integer patternId) {
        Integer userId = currentUserId();
        Pattern pattern = patternService.recoverPattern(patternId, userId);
        Map<String, Object> data = Map.of(
                "patternId", pattern.getPatternId(),
                "name", pattern.getName(),
                "updatedAt", pattern.getUpdatedAt());
        return Result.ok("恢复成功", data);
    }

    // ========================================================================
    // 6.2 作品广场
    // ========================================================================

    /**
     * 6.3.8 作品广场瀑布流（无需登录）
     * GET /api/patterns/square
     */
    @GetMapping("/square")
    public Result<PageResponse<PatternSquareItem>> getSquare(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String category) {
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 1);
        return Result.ok(patternService.getSquare(safePage, safeSize, sort, tag, category));
    }

    /**
     * 6.3.9 搜索作品（无需登录）
     * GET /api/patterns/square/search
     */
    @GetMapping("/square/search")
    public Result<PageResponse<PatternSquareItem>> searchPatterns(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String filters) {
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 1);
        return Result.ok(patternService.searchPatterns(keyword, safePage, safeSize, filters));
    }

    /**
     * 6.3.10 点赞（可选认证：游客也可点赞）
     * POST /api/patterns/{patternId}/like
     */
    @PostMapping("/{patternId}/like")
    public Result<Map<String, Object>> likePattern(@PathVariable Integer patternId) {
        Integer userId = currentUserId();
        return Result.ok("点赞成功", patternService.likePattern(patternId, userId));
    }

    /**
     * 6.3.11 取消点赞
     * DELETE /api/patterns/{patternId}/like
     */
    @DeleteMapping("/{patternId}/like")
    public Result<Map<String, Object>> unlikePattern(@PathVariable Integer patternId) {
        Integer userId = currentUserId();
        return Result.ok("已取消点赞", patternService.unlikePattern(patternId, userId));
    }

    /**
     * 6.3.12 收藏
     * POST /api/patterns/{patternId}/favorite
     */
    @PostMapping("/{patternId}/favorite")
    public Result<Map<String, Object>> favoritePattern(@PathVariable Integer patternId) {
        Integer userId = currentUserId();
        return Result.ok("收藏成功", patternService.favoritePattern(patternId, userId));
    }

    /**
     * 6.3.13 取消收藏
     * DELETE /api/patterns/{patternId}/favorite
     */
    @DeleteMapping("/{patternId}/favorite")
    public Result<Map<String, Object>> unfavoritePattern(@PathVariable Integer patternId) {
        Integer userId = currentUserId();
        return Result.ok("已取消收藏", patternService.unfavoritePattern(patternId, userId));
    }

    /**
     * 6.3.14 获取评论列表（无需登录）
     * GET /api/patterns/{patternId}/comments
     */
    @GetMapping("/{patternId}/comments")
    public Result<PageResponse<CommentResponse>> getComments(
            @PathVariable Integer patternId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 1);
        return Result.ok(patternService.getComments(patternId, safePage, safeSize));
    }

    /**
     * 6.3.15 发表评论
     * POST /api/patterns/{patternId}/comments
     */
    @PostMapping("/{patternId}/comments")
    public Result<CommentResponse> createComment(@PathVariable Integer patternId,
                                                  @Valid @RequestBody CreateCommentRequest request) {
        Integer userId = currentUserId();
        return Result.ok("评论成功", patternService.createComment(patternId, userId, request));
    }

    /**
     * 6.3.16 删除评论
     * DELETE /api/patterns/{patternId}/comments/{commentId}
     */
    @DeleteMapping("/{patternId}/comments/{commentId}")
    public Result<Void> deleteComment(@PathVariable Integer patternId,
                                      @PathVariable Integer commentId) {
        Integer userId = currentUserId();
        String role = currentRole();
        patternService.deleteComment(patternId, commentId, userId, role);
        return Result.ok("评论已删除", null);
    }

    /**
     * 6.3.17 举报作品
     * POST /api/patterns/{patternId}/report
     */
    @PostMapping("/{patternId}/report")
    public Result<Map<String, Object>> reportPattern(@PathVariable Integer patternId,
                                                      @Valid @RequestBody ReportRequest request) {
        Integer userId = currentUserId();
        return Result.ok("举报已提交", patternService.reportPattern(patternId, userId, request));
    }

    // ========================================================================
    // 安全上下文辅助
    // ========================================================================

    /** 获取当前登录用户ID（必须认证） */
    private Integer currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Integer) {
            return (Integer) auth.getPrincipal();
        }
        throw new com.example.zaran_design_backend.service.BusinessException(401, "未登录");
    }

    /** 获取当前登录用户ID（可选认证，未登录返回 null） */
    private Integer currentUserIdOptional() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Integer) {
            return (Integer) auth.getPrincipal();
        }
        return null;
    }

    /** 获取当前用户角色 */
    private String currentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities() != null) {
            return auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(a -> a.startsWith("ROLE_"))
                    .map(a -> a.substring(5).toLowerCase())
                    .findFirst()
                    .orElse("tourist");
        }
        return "tourist";
    }
}
