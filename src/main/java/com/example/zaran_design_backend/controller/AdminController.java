package com.example.zaran_design_backend.controller;

import com.example.zaran_design_backend.common.Result;
import com.example.zaran_design_backend.dto.*;
import com.example.zaran_design_backend.service.AdminService;
import com.example.zaran_design_backend.service.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 系统管理模块（第9模块）。
 * 所有接口仅管理员（admin）可访问。
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ========================================================================
    // 9.1 用户管理
    // ========================================================================

    /**
     * 9.4.1（设计文档 1） 用户列表
     * GET /api/admin/users?page=1&size=20&role=designer&status=1&keyword=xxx
     */
    @GetMapping("/users")
    public Result<PageResponse<AdminUserItem>> listUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        requireAdmin();
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 1);
        return Result.ok(adminService.listUsers(safePage, safeSize, role, status, keyword));
    }

    /**
     * 9.4.1（设计文档 2） 用户详情
     * GET /api/admin/users/{userId}
     */
    @GetMapping("/users/{userId}")
    public Result<AdminUserDetailResponse> getUserDetail(@PathVariable Integer userId) {
        requireAdmin();
        return Result.ok(adminService.getUserDetail(userId));
    }

    /**
     * 9.4.1（设计文档 3） 修改用户角色
     * PUT /api/admin/users/{userId}/role
     */
    @PutMapping("/users/{userId}/role")
    public Result<AdminUserDetailResponse> updateUserRole(
            @PathVariable Integer userId,
            @RequestBody UpdateUserRoleRequest request) {
        requireAdmin();
        Integer adminId = currentUserId();
        String adminName = currentUsername();
        return Result.ok("角色修改成功", adminService.updateUserRole(userId, request, adminId, adminName));
    }

    /**
     * 9.4.1（设计文档 4） 禁用/启用用户
     * PUT /api/admin/users/{userId}/status
     */
    @PutMapping("/users/{userId}/status")
    public Result<AdminUserDetailResponse> updateUserStatus(
            @PathVariable Integer userId,
            @RequestBody UpdateUserStatusRequest request) {
        requireAdmin();
        Integer adminId = currentUserId();
        String adminName = currentUsername();
        String msg = request.getStatus() == 0 ? "用户已禁用" : "用户已启用";
        return Result.ok(msg, adminService.updateUserStatus(userId, request, adminId, adminName));
    }

    /**
     * 9.4.1（设计文档 5） 用户统计
     * GET /api/admin/users/statistics
     */
    @GetMapping("/users/statistics")
    public Result<UserStatisticsResponse> getUserStatistics() {
        requireAdmin();
        return Result.ok(adminService.getUserStatistics());
    }

    // ========================================================================
    // 9.2 内容审核
    // ========================================================================

    /**
     * 9.4.2（设计文档 6） 待审核作品列表
     * GET /api/admin/audit/patterns?page=1&size=20
     */
    @GetMapping("/audit/patterns")
    public Result<PageResponse<PatternSquareItem>> getAuditPatterns(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        requireAdmin();
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 1);
        return Result.ok(adminService.getAuditPatterns(safePage, safeSize));
    }

    /**
     * 9.4.2（设计文档 7） 审核作品（通过/下架）
     * PUT /api/admin/audit/patterns/{patternId}
     */
    @PutMapping("/audit/patterns/{patternId}")
    public Result<Map<String, Object>> auditPattern(
            @PathVariable Integer patternId,
            @RequestBody AuditPatternRequest request) {
        requireAdmin();
        Integer adminId = currentUserId();
        String adminName = currentUsername();
        Map<String, Object> result = adminService.auditPattern(patternId, request, adminId, adminName);
        String msg = "approve".equals(request.getAction()) ? "审核通过" : "作品已下架";
        return Result.ok(msg, result);
    }

    /**
     * 9.4.2（设计文档 8） 被举报评论列表
     * GET /api/admin/audit/comments?page=1&size=20
     */
    @GetMapping("/audit/comments")
    public Result<PageResponse<CommentResponse>> getAuditComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        requireAdmin();
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 1);
        return Result.ok(adminService.getAuditComments(safePage, safeSize));
    }

    /**
     * 9.4.2（设计文档 9） 处理评论（保留/删除）
     * PUT /api/admin/audit/comments/{commentId}
     */
    @PutMapping("/audit/comments/{commentId}")
    public Result<Map<String, Object>> auditComment(
            @PathVariable Integer commentId,
            @RequestBody AuditCommentRequest request) {
        requireAdmin();
        Integer adminId = currentUserId();
        String adminName = currentUsername();
        Map<String, Object> result = adminService.auditComment(commentId, request, adminId, adminName);
        String msg = "approve".equals(request.getAction()) ? "评论已保留" : "评论已删除";
        return Result.ok(msg, result);
    }

    /**
     * 9.4.2（设计文档 10） 举报记录列表
     * GET /api/admin/audit/reports?page=1&size=20&status=pending
     */
    @GetMapping("/audit/reports")
    public Result<PageResponse<ReportItemResponse>> getReports(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        requireAdmin();
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 1);
        return Result.ok(adminService.getReports(safePage, safeSize, status));
    }

    // ========================================================================
    // 9.3 系统监控
    // ========================================================================

    /**
     * 9.4.3（设计文档 11） 操作日志查询
     * GET /api/admin/logs?page=1&size=50&userId=10001&operationType=DELETE_PATTERN&startDate=2026-05-01&endDate=2026-05-31
     */
    @GetMapping("/logs")
    public Result<PageResponse<LogItemResponse>> getLogs(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        requireAdmin();
        int safeSize = Math.min(Math.max(size, 1), 100);
        int safePage = Math.max(page, 1);
        return Result.ok(adminService.getLogs(safePage, safeSize, userId, operationType, startDate, endDate));
    }

    /**
     * 9.4.3（设计文档 12） 导出日志（简化实现）
     * GET /api/admin/logs/export
     */
    @GetMapping("/logs/export")
    public Result<Map<String, Object>> exportLogs() {
        requireAdmin();
        return Result.ok(adminService.exportLogs());
    }

    /**
     * 9.4.3（设计文档 13） API调用统计（占位）
     * GET /api/admin/api-stats
     */
    @GetMapping("/api-stats")
    public Result<Map<String, Object>> getApiStats() {
        requireAdmin();
        return Result.ok(adminService.getApiStats());
    }

    /**
     * 9.4.4（设计文档 14） 服务器性能指标
     * GET /api/admin/server-metrics
     */
    @GetMapping("/server-metrics")
    public Result<ServerMetricsResponse> getServerMetrics() {
        requireAdmin();
        return Result.ok(adminService.getServerMetrics());
    }

    /**
     * 9.4.3（设计文档 15） 生成任务队列状态（占位）
     * GET /api/admin/queue-metrics
     */
    @GetMapping("/queue-metrics")
    public Result<Map<String, Object>> getQueueMetrics() {
        requireAdmin();
        return Result.ok(adminService.getQueueMetrics());
    }

    /**
     * 9.4.3（设计文档 16） 手动触发数据备份（占位）
     * POST /api/admin/backup
     */
    @PostMapping("/backup")
    public Result<Map<String, Object>> triggerBackup() {
        requireAdmin();
        Integer adminId = currentUserId();
        String adminName = currentUsername();
        return Result.ok("备份任务已触发", adminService.triggerBackup(adminId, adminName));
    }

    // ========================================================================
    // 安全上下文辅助
    // ========================================================================

    /** 校验当前用户是否为管理员，非管理员直接抛异常 */
    private void requireAdmin() {
        String role = currentRole();
        if (!"admin".equalsIgnoreCase(role)) {
            throw new BusinessException(403, "仅管理员可访问系统管理接口");
        }
    }

    /** 获取当前登录用户ID */
    private Integer currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Integer) {
            return (Integer) auth.getPrincipal();
        }
        throw new BusinessException(401, "未登录");
    }

    /** 获取当前登录用户名 */
    private String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth.getName();
        }
        return "unknown";
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
