package com.example.zaran_design_backend.controller;

import com.example.zaran_design_backend.common.Result;
import com.example.zaran_design_backend.dto.*;
import com.example.zaran_design_backend.service.CollabService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 协同编辑模块（第7模块）。
 * 提供协同会话的创建、管理、操作历史查询和版本控制接口。
 */
@RestController
@RequestMapping("/api/collab")
public class CollabController {

    private final CollabService collabService;

    public CollabController(CollabService collabService) {
        this.collabService = collabService;
    }

    /**
     * 7.2.1 创建协同会话
     * POST /api/collab/sessions
     */
    @PostMapping("/sessions")
    public Result<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        Integer userId = currentUserId();
        return Result.ok("协同会话创建成功", collabService.createSession(userId, request));
    }

    /**
     * 7.2.2 我的协同会话列表
     * GET /api/collab/sessions
     */
    @GetMapping("/sessions")
    public Result<PageResponse<SessionListItem>> listSessions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Integer userId = currentUserId();
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 1);
        return Result.ok(collabService.listMySessions(userId, safePage, safeSize));
    }

    /**
     * 7.2.3 会话详情（参与者/管理员可查看）
     * GET /api/collab/sessions/{sessionId}
     */
    @GetMapping("/sessions/{sessionId}")
    public Result<SessionDetailResponse> getSessionDetail(@PathVariable Integer sessionId) {
        Integer userId = currentUserId();
        String role = currentRole();
        return Result.ok(collabService.getSessionDetail(sessionId, userId, role));
    }

    /**
     * 7.2.4 邀请参与者（仅所有者可操作）
     * POST /api/collab/sessions/{sessionId}/invite
     */
    @PostMapping("/sessions/{sessionId}/invite")
    public Result<SessionParticipantResponse> inviteParticipant(
            @PathVariable Integer sessionId,
            @Valid @RequestBody InviteRequest request) {
        Integer userId = currentUserId();
        return Result.ok("邀请成功", collabService.inviteParticipant(sessionId, userId, request));
    }

    /**
     * 7.2.5 修改参与者权限（仅所有者可操作）
     * PUT /api/collab/sessions/{sessionId}/participants/{targetUserId}
     */
    @PutMapping("/sessions/{sessionId}/participants/{targetUserId}")
    public Result<SessionParticipantResponse> updatePermission(
            @PathVariable Integer sessionId,
            @PathVariable Integer targetUserId,
            @Valid @RequestBody UpdatePermissionRequest request) {
        Integer ownerId = currentUserId();
        return Result.ok("权限修改成功",
                collabService.updateParticipantPermission(sessionId, targetUserId, ownerId, request.getPermission()));
    }

    /**
     * 7.2.6 移除参与者（仅所有者可操作）
     * DELETE /api/collab/sessions/{sessionId}/participants/{targetUserId}
     */
    @DeleteMapping("/sessions/{sessionId}/participants/{targetUserId}")
    public Result<Void> removeParticipant(
            @PathVariable Integer sessionId,
            @PathVariable Integer targetUserId) {
        Integer ownerId = currentUserId();
        collabService.removeParticipant(sessionId, targetUserId, ownerId);
        return Result.ok("移除成功", null);
    }

    /**
     * 7.2.7 获取操作历史（参与者可查看）
     * GET /api/collab/sessions/{sessionId}/history
     */
    @GetMapping("/sessions/{sessionId}/history")
    public Result<OperationHistoryResponse> getOperationHistory(
            @PathVariable Integer sessionId,
            @RequestParam(required = false) Integer versionStart,
            @RequestParam(required = false) Integer versionEnd,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size) {
        Integer userId = currentUserId();
        int safeSize = Math.min(Math.max(size, 1), 100);
        int safePage = Math.max(page, 1);
        return Result.ok(collabService.getOperationHistory(sessionId, userId, versionStart, versionEnd, safePage, safeSize));
    }

    /**
     * 7.2.8 手动保存版本（参与者有编辑权限可操作）
     * POST /api/collab/sessions/{sessionId}/versions
     */
    @PostMapping("/sessions/{sessionId}/versions")
    public Result<CollabVersionResponse> saveVersion(
            @PathVariable Integer sessionId,
            @Valid @RequestBody SaveVersionRequest request) {
        Integer userId = currentUserId();
        return Result.ok("版本保存成功", collabService.saveVersion(sessionId, userId, request));
    }

    /**
     * 7.2.9 回滚到版本（仅所有者可操作）
     * POST /api/collab/sessions/{sessionId}/versions/{versionId}/rollback
     */
    @PostMapping("/sessions/{sessionId}/versions/{versionId}/rollback")
    public Result<CollabVersionResponse> rollbackVersion(
            @PathVariable Integer sessionId,
            @PathVariable Integer versionId) {
        Integer ownerId = currentUserId();
        CollabVersionResponse response = collabService.rollbackVersion(sessionId, versionId, ownerId);
        return Result.ok("已回滚到版本 " + response.getVersionNumber(), response);
    }

    /**
     * 关闭协同会话（仅所有者可操作）
     * POST /api/collab/sessions/{sessionId}/close
     */
    @PostMapping("/sessions/{sessionId}/close")
    public Result<Void> closeSession(@PathVariable Integer sessionId) {
        Integer ownerId = currentUserId();
        collabService.closeSession(sessionId, ownerId);
        return Result.ok("会话已关闭", null);
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
