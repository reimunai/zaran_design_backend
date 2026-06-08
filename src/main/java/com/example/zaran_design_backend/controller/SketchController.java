package com.example.zaran_design_backend.controller;

import com.example.zaran_design_backend.common.Result;
import com.example.zaran_design_backend.dto.*;
import com.example.zaran_design_backend.entity.Sketch;
import com.example.zaran_design_backend.service.SketchService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 草图绘制模块（第4模块）。
 * 业务异常由 GlobalExceptionHandler 统一转换为带文档错误码的响应。
 */
@RestController
@RequestMapping("/api/sketches")
public class SketchController {

    private final SketchService sketchService;

    public SketchController(SketchService sketchService) {
        this.sketchService = sketchService;
    }

    /**
     * 4.2.13 获取草图分类树（无需登录）
     * GET /api/sketches/categories
     * 注意：该映射需置于 /{sketchId} 之前，避免 "categories" 被当作路径变量。
     */
    @GetMapping("/categories")
    public Result<List<CategoryNode>> getCategories() {
        return Result.ok(sketchService.getCategoryTree());
    }

    /**
     * 4.2.14 新增草图分类（仅传承人/管理员）
     * POST /api/sketches/categories
     */
    @PostMapping("/categories")
    public Result<CategoryNode> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        String role = currentRole();
        return Result.ok("分类创建成功", sketchService.createCategory(request, role));
    }

    /**
     * 4.2.1 创建新草图
     * POST /api/sketches
     */
    @PostMapping
    public Result<SketchResponse> createSketch(@Valid @RequestBody CreateSketchRequest request) {
        Integer userId = currentUserId();
        return Result.ok("创建成功", sketchService.createSketch(userId, request));
    }

    /**
     * 4.2.2 查询我的草图列表
     * GET /api/sketches
     */
    @GetMapping
    public Result<PageResponse<SketchListItem>> listSketches(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "false") boolean includeDeleted) {
        Integer userId = currentUserId();
        int safeSize = Math.min(Math.max(size, 1), 50); // 最大50
        int safePage = Math.max(page, 1);
        PageResponse<SketchListItem> data = sketchService.listMySketches(
                userId, safePage, safeSize, keyword, categoryId, isPublic, sort, includeDeleted);
        return Result.ok(data);
    }

    /**
     * 4.2.3 获取草图详情
     * GET /api/sketches/{sketchId}
     */
    @GetMapping("/{sketchId}")
    public Result<SketchDetailResponse> getSketchDetail(@PathVariable Integer sketchId) {
        Integer userId = currentUserId();
        String role = currentRole();
        return Result.ok(sketchService.getSketchDetail(sketchId, userId, role));
    }

    /**
     * 4.2.4 保存草图
     * PUT /api/sketches/{sketchId}
     */
    @PutMapping("/{sketchId}")
    public Result<SketchDetailResponse> saveSketch(@PathVariable Integer sketchId,
                                                   @Valid @RequestBody SaveSketchRequest request) {
        Integer userId = currentUserId();
        return Result.ok("保存成功", sketchService.saveSketch(sketchId, userId, request));
    }

    /**
     * 4.2.5 设置草图公开/私密
     * PATCH /api/sketches/{sketchId}/visibility
     */
    @PatchMapping("/{sketchId}/visibility")
    public Result<Map<String, Object>> setVisibility(@PathVariable Integer sketchId,
                                                     @Valid @RequestBody VisibilityRequest request) {
        Integer userId = currentUserId();
        Sketch sketch = sketchService.setVisibility(sketchId, userId, request.getIsPublic());
        Map<String, Object> data = new HashMap<>();
        data.put("sketchId", sketch.getSketchId());
        data.put("isPublic", sketch.getIsPublic());
        return Result.ok(sketch.getIsPublic() ? "已设为公开" : "已设为私密", data);
    }

    /**
     * 4.2.6 删除草图（软删除）
     * DELETE /api/sketches/{sketchId}
     */
    @DeleteMapping("/{sketchId}")
    public Result<Map<String, Object>> deleteSketch(@PathVariable Integer sketchId) {
        Integer userId = currentUserId();
        LocalDateTime deletedAt = sketchService.softDelete(sketchId, userId);
        Map<String, Object> data = new HashMap<>();
        data.put("sketchId", sketchId);
        data.put("deletedAt", deletedAt);
        return Result.ok("已移入回收站", data);
    }

    /**
     * 4.2.7 恢复已删除草图
     * POST /api/sketches/{sketchId}/recover
     */
    @PostMapping("/{sketchId}/recover")
    public Result<Map<String, Object>> recoverSketch(@PathVariable Integer sketchId) {
        Integer userId = currentUserId();
        Sketch sketch = sketchService.recover(sketchId, userId);
        Map<String, Object> data = new HashMap<>();
        data.put("sketchId", sketch.getSketchId());
        data.put("name", sketch.getName());
        data.put("updatedAt", sketch.getUpdatedAt());
        return Result.ok("恢复成功", data);
    }

    /**
     * 4.2.8 创建版本快照
     * POST /api/sketches/{sketchId}/versions
     */
    @PostMapping("/{sketchId}/versions")
    public Result<CreateVersionResponse> createVersion(@PathVariable Integer sketchId,
                                                       @Valid @RequestBody CreateVersionRequest request) {
        Integer userId = currentUserId();
        return Result.ok("版本保存成功", sketchService.createVersion(sketchId, userId, request));
    }

    /**
     * 4.2.9 获取版本历史
     * GET /api/sketches/{sketchId}/versions
     */
    @GetMapping("/{sketchId}/versions")
    public Result<PageResponse<VersionResponse>> listVersions(
            @PathVariable Integer sketchId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Integer userId = currentUserId();
        String role = currentRole();
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 1);
        return Result.ok(sketchService.listVersions(sketchId, userId, role, safePage, safeSize));
    }

    /**
     * 4.2.10 获取指定版本详情
     * GET /api/sketches/{sketchId}/versions/{versionId}
     */
    @GetMapping("/{sketchId}/versions/{versionId}")
    public Result<VersionDetailResponse> getVersionDetail(@PathVariable Integer sketchId,
                                                          @PathVariable Integer versionId) {
        Integer userId = currentUserId();
        String role = currentRole();
        return Result.ok(sketchService.getVersionDetail(sketchId, versionId, userId, role));
    }

    /**
     * 4.2.11 回退到指定版本
     * POST /api/sketches/{sketchId}/versions/{versionId}/restore
     */
    @PostMapping("/{sketchId}/versions/{versionId}/restore")
    public Result<RestoreVersionResponse> restoreVersion(@PathVariable Integer sketchId,
                                                         @PathVariable Integer versionId) {
        Integer userId = currentUserId();
        RestoreVersionResponse response = sketchService.restoreVersion(sketchId, versionId, userId);
        return Result.ok("已回退到版本 " + response.getRestoredFrom(), response);
    }

    /**
     * 4.2.12 导入本地图片
     * POST /api/sketches/{sketchId}/import (multipart/form-data)
     */
    @PostMapping("/{sketchId}/import")
    public Result<ImportImageResponse> importImage(
            @PathVariable Integer sketchId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "asLayer", defaultValue = "true") boolean asLayer,
            @RequestParam(value = "layerName", required = false) String layerName) {
        Integer userId = currentUserId();
        return Result.ok("导入成功", sketchService.importImage(sketchId, userId, file, asLayer, layerName));
    }

    // ============================ 安全上下文辅助 ============================

    private Integer currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Integer) {
            return (Integer) auth.getPrincipal();
        }
        throw new com.example.zaran_design_backend.service.BusinessException(401, "未登录");
    }

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
