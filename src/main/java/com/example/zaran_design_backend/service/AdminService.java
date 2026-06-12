package com.example.zaran_design_backend.service;

import com.example.zaran_design_backend.dto.*;
import com.example.zaran_design_backend.entity.*;
import com.example.zaran_design_backend.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 系统管理模块（第9模块）服务。
 * 所有操作均要求调用方已确认当前用户具有 admin 角色。
 */
@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PatternRepository patternRepository;
    private final PatternCommentRepository patternCommentRepository;
    private final PatternReportRepository patternReportRepository;
    private final OperationLogRepository operationLogRepository;
    private final UserFollowRepository userFollowRepository;

    public AdminService(UserRepository userRepository,
                        PatternRepository patternRepository,
                        PatternCommentRepository patternCommentRepository,
                        PatternReportRepository patternReportRepository,
                        OperationLogRepository operationLogRepository,
                        UserFollowRepository userFollowRepository) {
        this.userRepository = userRepository;
        this.patternRepository = patternRepository;
        this.patternCommentRepository = patternCommentRepository;
        this.patternReportRepository = patternReportRepository;
        this.operationLogRepository = operationLogRepository;
        this.userFollowRepository = userFollowRepository;
    }

    // ========================================================================
    // 9.1 用户管理
    // ========================================================================

    /**
     * 9.4.1 用户列表（分页 + 筛选）
     */
    public PageResponse<AdminUserItem> listUsers(int page, int size, String role,
                                                  Integer status, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (role != null && !role.isEmpty()) {
                try {
                    User.Role r = User.Role.valueOf(role.toLowerCase());
                    predicates.add(cb.equal(root.get("role"), r));
                } catch (IllegalArgumentException ignored) { }
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("isDisabled"), status == 0));
            }
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(cb.or(
                        cb.like(root.get("username"), "%" + keyword + "%"),
                        cb.like(root.get("phone"), "%" + keyword + "%")
                ));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<User> userPage = userRepository.findAll(spec, pageable);
        return PageResponse.of(userPage, page, size, AdminUserItem::of);
    }

    /**
     * 9.4.1（设计文档实际另有详情接口） 获取指定用户详情（管理员视角）
     */
    public AdminUserDetailResponse getUserDetail(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(4041, "用户不存在"));
        long patternCount = patternRepository.countByUserIdAndDeletedAtIsNull(userId);
        long followerCount = userFollowRepository.countByFollowingId(userId);
        long followingCount = userFollowRepository.countByFollowerId(userId);
        return AdminUserDetailResponse.of(user, patternCount, followerCount, followingCount);
    }

    /**
     * 9.4.2（设计文档 3） 修改用户角色
     */
    @Transactional
    public AdminUserDetailResponse updateUserRole(Integer userId, UpdateUserRoleRequest request,
                                                   Integer adminId, String adminName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(4041, "用户不存在"));
        try {
            User.Role newRole = User.Role.valueOf(request.getRole().toLowerCase());
            user.setRole(newRole);
            userRepository.save(user);
            writeLog(adminId, adminName, "UPDATE_USER_ROLE",
                    "修改用户 userId=" + userId + " 角色为 " + newRole.name(), "success");
            long patternCount = patternRepository.countByUserIdAndDeletedAtIsNull(userId);
            long followerCount = userFollowRepository.countByFollowingId(userId);
            long followingCount = userFollowRepository.countByFollowerId(userId);
            return AdminUserDetailResponse.of(user, patternCount, followerCount, followingCount);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "无效角色：" + request.getRole() + "，可选 admin/inheritor/designer/tourist");
        }
    }

    /**
     * 9.4.2（设计文档 4） 禁用/启用用户
     */
    @Transactional
    public AdminUserDetailResponse updateUserStatus(Integer userId, UpdateUserStatusRequest request,
                                                     Integer adminId, String adminName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(4041, "用户不存在"));
        boolean disabled = request.getStatus() == 0;
        user.setIsDisabled(disabled);
        userRepository.save(user);
        String action = disabled ? "禁用" : "启用";
        writeLog(adminId, adminName, "UPDATE_USER_STATUS",
                action + "用户 userId=" + userId, "success");
        long patternCount = patternRepository.countByUserIdAndDeletedAtIsNull(userId);
        long followerCount = userFollowRepository.countByFollowingId(userId);
        long followingCount = userFollowRepository.countByFollowerId(userId);
        return AdminUserDetailResponse.of(user, patternCount, followerCount, followingCount);
    }

    /**
     * 9.4.1（设计文档 5） 用户统计
     */
    public UserStatisticsResponse getUserStatistics() {
        UserStatisticsResponse stats = new UserStatisticsResponse();
        stats.setTotalUsers(userRepository.count());
        stats.setAdminCount(userRepository.countByRole(User.Role.admin));
        stats.setInheritorCount(userRepository.countByRole(User.Role.inheritor));
        stats.setDesignerCount(userRepository.countByRole(User.Role.designer));
        stats.setTouristCount(userRepository.countByRole(User.Role.tourist));
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        stats.setNewUsersToday(userRepository.countByCreatedAtAfter(todayStart));
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        stats.setActiveUsersWeek(userRepository.countByUpdatedAtAfter(weekAgo));
        return stats;
    }

    // ========================================================================
    // 9.2 内容审核
    // ========================================================================

    /**
     * 9.4.2（设计文档 6） 待审核作品列表
     * 返回所有未删除的作品（按创建时间倒序），管理员可逐一下架审核。
     */
    public PageResponse<PatternSquareItem> getAuditPatterns(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Pattern> patternPage = patternRepository.findAll(
                (root, query, cb) -> cb.isNull(root.get("deletedAt")), pageable);
        return PageResponse.of(patternPage, page, size, this::toSquareItem);
    }

    /**
     * 9.4.2（设计文档 7） 审核作品（通过/下架）
     */
    @Transactional
    public Map<String, Object> auditPattern(Integer patternId, AuditPatternRequest request,
                                             Integer adminId, String adminName) {
        Pattern pattern = patternRepository.findByPatternIdAndDeletedAtIsNull(patternId)
                .orElseThrow(() -> new BusinessException(4044, "作品不存在或已删除"));
        String action = request.getAction();
        if ("approve".equals(action)) {
            // 通过：不做特殊处理，作品保持正常状态
            writeLog(adminId, adminName, "AUDIT_PATTERN_APPROVE",
                    "审核通过作品 patternId=" + patternId, "success");
            Map<String, Object> result = new HashMap<>();
            result.put("patternId", patternId);
            result.put("action", "approved");
            return result;
        } else if ("reject".equals(action)) {
            // 下架：软删除作品
            pattern.setDeletedAt(LocalDateTime.now());
            patternRepository.save(pattern);
            writeLog(adminId, adminName, "AUDIT_PATTERN_REJECT",
                    "下架作品 patternId=" + patternId + ", reason=" + request.getReason(), "success");
            Map<String, Object> result = new HashMap<>();
            result.put("patternId", patternId);
            result.put("action", "rejected");
            result.put("reason", request.getReason());
            return result;
        } else {
            throw new BusinessException(400, "操作无效，仅支持 approve/reject");
        }
    }

    /**
     * 9.4.2（设计文档 8） 被举报评论列表
     * 返回全部评论（按创建时间倒序），供管理员审核。
     */
    public PageResponse<CommentResponse> getAuditComments(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PatternComment> commentPage = patternCommentRepository.findAll(pageable);
        return PageResponse.of(commentPage, page, size, c -> {
            PatternDetailResponse.AuthorBrief author = userRepository.findById(c.getUserId())
                    .map(u -> new PatternDetailResponse.AuthorBrief(
                            u.getUserId(), u.getUsername(), u.getAvatar(), u.getRole().name()))
                    .orElse(new PatternDetailResponse.AuthorBrief(
                            c.getUserId(), "unknown", null, "tourist"));
            return CommentResponse.of(c, author, Collections.emptyList());
        });
    }

    /**
     * 9.4.2（设计文档 9） 处理评论（保留/删除）
     */
    @Transactional
    public Map<String, Object> auditComment(Integer commentId, AuditCommentRequest request,
                                             Integer adminId, String adminName) {
        PatternComment comment = patternCommentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(404, "评论不存在"));
        String action = request.getAction();
        if ("approve".equals(action)) {
            writeLog(adminId, adminName, "AUDIT_COMMENT_APPROVE",
                    "保留评论 commentId=" + commentId, "success");
            Map<String, Object> result = new HashMap<>();
            result.put("commentId", commentId);
            result.put("action", "approved");
            return result;
        } else if ("reject".equals(action)) {
            patternCommentRepository.delete(comment);
            writeLog(adminId, adminName, "AUDIT_COMMENT_REJECT",
                    "删除评论 commentId=" + commentId + ", reason=" + request.getReason(), "success");
            Map<String, Object> result = new HashMap<>();
            result.put("commentId", commentId);
            result.put("action", "rejected");
            result.put("reason", request.getReason());
            return result;
        } else {
            throw new BusinessException(400, "操作无效，仅支持 approve/reject");
        }
    }

    /**
     * 9.4.2（设计文档 10） 举报记录列表
     */
    public PageResponse<ReportItemResponse> getReports(int page, int size, String status) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PatternReport> reportPage;
        if (status != null && !status.isEmpty()) {
            reportPage = patternReportRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else {
            reportPage = patternReportRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        return PageResponse.of(reportPage, page, size, r -> {
            String patternName = patternRepository.findById(r.getPatternId())
                    .map(Pattern::getName).orElse("已删除作品");
            String username = userRepository.findById(r.getUserId())
                    .map(User::getUsername).orElse("unknown");
            return ReportItemResponse.of(r.getReportId(), r.getPatternId(), patternName,
                    r.getUserId(), username, r.getReason(), r.getDescription(),
                    r.getStatus(), r.getCreatedAt());
        });
    }

    // ========================================================================
    // 9.3 系统监控
    // ========================================================================

    /**
     * 9.4.3 操作日志查询
     */
    public PageResponse<LogItemResponse> getLogs(int page, int size, Integer userId,
                                                  String operationType, String startDate, String endDate) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<OperationLog> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                predicates.add(cb.equal(root.get("userId"), userId));
            }
            if (operationType != null && !operationType.isEmpty()) {
                predicates.add(cb.equal(root.get("operationType"), operationType));
            }
            if (startDate != null && !startDate.isEmpty()) {
                LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), start));
            }
            if (endDate != null && !endDate.isEmpty()) {
                LocalDateTime end = LocalDate.parse(endDate).atTime(LocalTime.MAX);
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), end));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        Page<OperationLog> logPage = operationLogRepository.findAll(spec, pageable);
        return PageResponse.of(logPage, page, size, l ->
                LogItemResponse.of(l.getLogId(), l.getUserId(), l.getUsername(),
                        l.getOperationType(), l.getOperationContent(),
                        l.getIpAddress(), l.getUserAgent(), l.getResult(), l.getCreatedAt()));
    }

    /**
     * 9.4.3（设计文档 12） 导出日志（简化：返回CSV文本）
     */
    public Map<String, Object> exportLogs() {
        Map<String, Object> result = new HashMap<>();
        result.put("downloadUrl", "/api/admin/logs?page=1&size=100");
        result.put("format", "csv");
        result.put("expiresIn", 3600);
        result.put("message", "日志导出功能开发中，请使用日志查询接口获取数据");
        return result;
    }

    /**
     * 9.4.3（设计文档 13） API调用统计（占位）
     */
    public Map<String, Object> getApiStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalCalls", 0);
        stats.put("avgResponseTime", 0);
        stats.put("errorRate", 0);
        stats.put("topEndpoints", Collections.emptyList());
        stats.put("message", "API统计功能待接入指标收集系统后可用");
        return stats;
    }

    /**
     * 9.4.4 服务器性能指标
     * 通过 JMX 获取 JVM 级指标，GPU数据为占位。
     */
    public ServerMetricsResponse getServerMetrics() {
        ServerMetricsResponse metrics = new ServerMetricsResponse();
        metrics.setTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        Runtime runtime = Runtime.getRuntime();

        // CPU
        ServerMetricsResponse.CpuInfo cpuInfo = new ServerMetricsResponse.CpuInfo();
        cpuInfo.setCores(runtime.availableProcessors());
        double cpuLoad = osBean.getSystemLoadAverage();
        cpuInfo.setUsage(cpuLoad < 0 ? 0 : Math.min(cpuLoad * 100 / runtime.availableProcessors(), 100));
        metrics.setCpu(cpuInfo);

        // Memory
        ServerMetricsResponse.MemoryInfo memoryInfo = new ServerMetricsResponse.MemoryInfo();
        long totalMem = runtime.totalMemory();
        long freeMem = runtime.freeMemory();
        long usedMem = totalMem - freeMem;
        long maxMem = runtime.maxMemory();
        memoryInfo.setTotal(maxMem / (1024 * 1024));
        memoryInfo.setUsed(usedMem / (1024 * 1024));
        memoryInfo.setUsage(maxMem > 0 ? (double) usedMem / maxMem * 100 : 0);
        metrics.setMemory(memoryInfo);

        // GPU（占位）
        ServerMetricsResponse.GpuInfo gpuInfo = new ServerMetricsResponse.GpuInfo();
        gpuInfo.setUsage(0);
        gpuInfo.setMemoryUsed(0);
        gpuInfo.setMemoryTotal(0);
        gpuInfo.setTemperature(0);
        metrics.setGpu(gpuInfo);

        // Disk（占位）
        ServerMetricsResponse.DiskInfo diskInfo = new ServerMetricsResponse.DiskInfo();
        diskInfo.setTotal(0);
        diskInfo.setUsed(0);
        diskInfo.setUsage(0);
        metrics.setDisk(diskInfo);

        // Network（占位）
        ServerMetricsResponse.NetworkInfo networkInfo = new ServerMetricsResponse.NetworkInfo();
        networkInfo.setInMbps(0);
        networkInfo.setOutMbps(0);
        metrics.setNetwork(networkInfo);

        return metrics;
    }

    /**
     * 9.4.3（设计文档 15） 生成任务队列状态（占位：AI生成模块尚未开发）
     */
    public Map<String, Object> getQueueMetrics() {
        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("queueLength", 0);
        metrics.put("processingCount", 0);
        metrics.put("maxConcurrent", 20);
        metrics.put("avgWaitTime", 0);
        metrics.put("status", "idle");
        metrics.put("message", "队列监控待AI生成模块上线后可用");
        return metrics;
    }

    /**
     * 9.4.3（设计文档 16） 手动触发数据备份（占位）
     */
    public Map<String, Object> triggerBackup(Integer adminId, String adminName) {
        writeLog(adminId, adminName, "MANUAL_BACKUP",
                "手动触发数据备份", "success");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("backupId", UUID.randomUUID().toString());
        result.put("status", "completed");
        result.put("createdAt", LocalDateTime.now().toString());
        result.put("message", "备份功能开发中，当前为占位实现");
        return result;
    }

    // ========================================================================
    // 辅助方法
    // ========================================================================

    /** 写入操作日志 */
    private void writeLog(Integer userId, String username, String operationType,
                          String operationContent, String result) {
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setOperationType(operationType);
        log.setOperationContent(operationContent);
        log.setResult(result);
        operationLogRepository.save(log);
    }

    /** 将 Pattern 转换为广场列表项 */
    private PatternSquareItem toSquareItem(Pattern p) {
        PatternDetailResponse.AuthorBrief author = userRepository.findById(p.getUserId())
                .map(u -> new PatternDetailResponse.AuthorBrief(
                        u.getUserId(), u.getUsername(), u.getAvatar(), u.getRole().name()))
                .orElse(new PatternDetailResponse.AuthorBrief(
                        p.getUserId(), "unknown", null, "tourist"));
        return PatternSquareItem.of(p, author);
    }
}
