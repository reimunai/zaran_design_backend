package com.example.zaran_design_backend.controller;

import com.example.zaran_design_backend.common.Result;
import com.example.zaran_design_backend.dto.*;
import com.example.zaran_design_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取当前用户信息
     * GET /api/users/profile
     */
    @GetMapping("/profile")
    public Result<UserProfileResponse> getProfile() {
        try {
            Integer userId = getCurrentUserId();
            UserProfileResponse response = userService.getProfile(userId);
            return Result.ok(response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 修改个人信息
     * PUT /api/users/profile
     */
    @PutMapping("/profile")
    public Result<UserProfileResponse> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            Integer userId = getCurrentUserId();
            UserProfileResponse response = userService.updateProfile(userId, request);
            return Result.ok("修改成功", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 修改密码
     * PUT /api/users/password
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        try {
            Integer userId = getCurrentUserId();
            userService.changePassword(userId, request);
            return Result.ok("密码修改成功", null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 申请成为设计师
     * POST /api/users/apply-designer
     */
    @PostMapping("/apply-designer")
    public Result<ApplicationResponse> applyDesigner(@Valid @RequestBody DesignerApplyRequest request) {
        try {
            Integer userId = getCurrentUserId();
            ApplicationResponse response = userService.applyDesigner(userId, request);
            return Result.ok("申请已提交，等待传承人审核", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 查看认证申请列表
     * GET /api/users/applications
     */
    @GetMapping("/applications")
    public Result<List<ApplicationResponse>> getApplications() {
        try {
            String role = getCurrentUserRole();
            List<ApplicationResponse> list = userService.getApplications(role);
            return Result.ok(list);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 审核通过设计师申请
     * PUT /api/users/applications/{applicationId}/approve
     */
    @PutMapping("/applications/{applicationId}/approve")
    public Result<ApplicationResponse> approveApplication(
            @PathVariable Integer applicationId,
            @RequestBody(required = false) ApplicationReviewRequest request) {
        try {
            Integer reviewerId = getCurrentUserId();
            ApplicationReviewRequest reviewRequest = request != null ? request : new ApplicationReviewRequest();
            ApplicationResponse response = userService.approveApplication(applicationId, reviewerId, reviewRequest);
            return Result.ok("审核通过", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 拒绝设计师申请
     * PUT /api/users/applications/{applicationId}/reject
     */
    @PutMapping("/applications/{applicationId}/reject")
    public Result<ApplicationResponse> rejectApplication(
            @PathVariable Integer applicationId,
            @RequestBody(required = false) ApplicationReviewRequest request) {
        try {
            Integer reviewerId = getCurrentUserId();
            ApplicationReviewRequest reviewRequest = request != null ? request : new ApplicationReviewRequest();
            ApplicationResponse response = userService.rejectApplication(applicationId, reviewerId, reviewRequest);
            return Result.ok("已拒绝", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 查看指定用户主页
     * GET /api/users/{userId}
     */
    @GetMapping("/{userId}")
    public Result<UserPublicResponse> getUserPublic(@PathVariable Integer userId) {
        try {
            Integer currentUserId = getCurrentUserIdOrNull();
            UserPublicResponse response = userService.getUserPublic(userId, currentUserId);
            return Result.ok(response);
        } catch (RuntimeException e) {
            return Result.error(4041, e.getMessage());
        }
    }

    /**
     * 查看用户公开作品（暂未实现）
     * GET /api/users/{userId}/patterns
     */
    @GetMapping("/{userId}/patterns")
    public Result<Void> getUserPatterns(@PathVariable Integer userId) {
        return Result.ok("success", null);
    }

    /**
     * 查看用户公开草图（暂未实现）
     * GET /api/users/{userId}/sketches
     */
    @GetMapping("/{userId}/sketches")
    public Result<Void> getUserSketches(@PathVariable Integer userId) {
        return Result.ok("success", null);
    }

    /**
     * 关注用户
     * POST /api/users/{userId}/follow
     */
    @PostMapping("/{userId}/follow")
    public Result<Void> followUser(@PathVariable Integer userId) {
        try {
            Integer followerId = getCurrentUserId();
            userService.followUser(followerId, userId);
            return Result.ok("关注成功", null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 取消关注
     * DELETE /api/users/{userId}/follow
     */
    @DeleteMapping("/{userId}/follow")
    public Result<Void> unfollowUser(@PathVariable Integer userId) {
        try {
            Integer followerId = getCurrentUserId();
            userService.unfollowUser(followerId, userId);
            return Result.ok("已取消关注", null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 从SecurityContext获取当前用户ID
     */
    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Integer) {
            return (Integer) auth.getPrincipal();
        }
        throw new RuntimeException("未登录");
    }

    /**
     * 获取当前用户ID（可为null，用于公开接口）
     */
    private Integer getCurrentUserIdOrNull() {
        try {
            return getCurrentUserId();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取当前用户角色
     */
    private String getCurrentUserRole() {
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
