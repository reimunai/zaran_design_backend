package com.example.zaran_design_backend.controller;

import com.example.zaran_design_backend.common.Result;
import com.example.zaran_design_backend.dto.*;
import com.example.zaran_design_backend.security.JwtUtils;
import com.example.zaran_design_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    public AuthController(AuthService authService, JwtUtils jwtUtils) {
        this.authService = authService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 用户注册
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public Result<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = authService.register(request);
            return Result.ok("注册成功", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 账号密码登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return Result.ok("登录成功", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 手机验证码登录（暂未实现，预留接口）
     * POST /api/auth/login/phone
     */
    @PostMapping("/login/phone")
    public Result<Void> loginByPhone() {
        return Result.error(400, "手机验证码登录功能开发中");
    }

    /**
     * 微信扫码登录（暂未实现，预留接口）
     * POST /api/auth/login/wechat
     */
    @PostMapping("/login/wechat")
    public Result<Void> loginByWechat() {
        return Result.error(400, "微信扫码登录功能开发中");
    }

    /**
     * QQ登录（暂未实现，预留接口）
     * POST /api/auth/login/qq
     */
    @PostMapping("/login/qq")
    public Result<Void> loginByQQ() {
        return Result.error(400, "QQ登录功能开发中");
    }

    /**
     * 刷新Token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public Result<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            TokenResponse response = authService.refreshToken(request);
            return Result.ok(response);
        } catch (RuntimeException e) {
            return Result.error(4012, e.getMessage());
        }
    }

    /**
     * 退出登录
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT无状态，客户端删除Token即可。服务端返回成功。
        return Result.ok("退出成功", null);
    }

    /**
     * 忘记密码重置
     * POST /api/auth/password/reset
     */
    @PostMapping("/password/reset")
    public Result<Void> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        try {
            authService.resetPassword(request);
            return Result.ok("密码重置成功", null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * [DEBUG] 注册/重置管理员账号（无需认证，仅调试用）
     * POST /api/auth/register/admin
     */
    @PostMapping("/register/admin")
    public Result<LoginResponse> registerAdmin(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.registerAdmin(request.getUsername(), request.getPassword());
            return Result.ok("管理员账号已就绪", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }
}
