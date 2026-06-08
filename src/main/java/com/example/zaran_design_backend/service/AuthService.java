package com.example.zaran_design_backend.service;

import com.example.zaran_design_backend.dto.*;
import com.example.zaran_design_backend.entity.User;
import com.example.zaran_design_backend.repository.UserRepository;
import com.example.zaran_design_backend.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    /**
     * 用户注册
     */
    public RegisterResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (request.getPhone() != null && !request.getPhone().isEmpty()
                && userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && !request.getEmail().isEmpty()
                && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 手机号和邮箱至少填一个
        if ((request.getPhone() == null || request.getPhone().isEmpty())
                && (request.getEmail() == null || request.getEmail().isEmpty())) {
            throw new RuntimeException("手机号和邮箱至少填写一个");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());

        // 设置角色
        try {
            user.setRole(User.Role.valueOf(request.getRole()));
        } catch (IllegalArgumentException e) {
            user.setRole(User.Role.tourist);
        }

        userRepository.save(user);

        // 生成Token
        String accessToken = jwtUtils.generateAccessToken(user.getUserId(), user.getUsername(), user.getRole().name());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId(), user.getUsername());

        return RegisterResponse.of(user, accessToken, refreshToken, jwtUtils.getAccessTokenExpiration());
    }

    /**
     * 账号密码登录（支持用户名/手机号/邮箱）
     */
    public LoginResponse login(LoginRequest request) {
        // 尝试用用户名/手机号/邮箱查找用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseGet(() -> userRepository.findByPhone(request.getUsername())
                        .orElseGet(() -> userRepository.findByEmail(request.getUsername())
                                .orElse(null)));

        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户是否被禁用
        if (user.getIsDisabled()) {
            throw new RuntimeException("账号已被禁用，请联系管理员");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 生成Token
        String accessToken = jwtUtils.generateAccessToken(user.getUserId(), user.getUsername(), user.getRole().name());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId(), user.getUsername());

        return LoginResponse.of(accessToken, refreshToken, jwtUtils.getAccessTokenExpiration(), user);
    }

    /**
     * 刷新Token
     */
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 验证refreshToken
        if (!jwtUtils.validateToken(refreshToken) || !jwtUtils.isRefreshToken(refreshToken)) {
            throw new RuntimeException("refreshToken无效或已过期");
        }

        Integer userId = jwtUtils.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (user.getIsDisabled()) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成新的Token对
        String newAccessToken = jwtUtils.generateAccessToken(user.getUserId(), user.getUsername(), user.getRole().name());
        String newRefreshToken = jwtUtils.generateRefreshToken(user.getUserId(), user.getUsername());

        return TokenResponse.of(newAccessToken, newRefreshToken, jwtUtils.getAccessTokenExpiration());
    }

    /**
     * 注册/重置管理员账号（仅调试用）
     * 如果管理员已存在则重置密码，不存在则创建
     */
    public LoginResponse registerAdmin(String username, String password) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user != null) {
            // 已存在则重置密码并确保角色为admin
            user.setPassword(passwordEncoder.encode(password));
            if (user.getRole() != User.Role.admin) {
                user.setRole(User.Role.admin);
            }
            user.setIsDisabled(false);
            userRepository.save(user);
        } else {
            // 创建新的管理员账号
            user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(User.Role.admin);
            userRepository.save(user);
        }

        String accessToken = jwtUtils.generateAccessToken(user.getUserId(), user.getUsername(), user.getRole().name());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId(), user.getUsername());

        return LoginResponse.of(accessToken, refreshToken, jwtUtils.getAccessTokenExpiration(), user);
    }

    /**
     * 重置密码（简化版：验证码校验 + 修改密码）
     */
    public void resetPassword(PasswordResetRequest request) {
        // 手机号和邮箱二选一
        if ((request.getPhone() == null || request.getPhone().isEmpty())
                && (request.getEmail() == null || request.getEmail().isEmpty())) {
            throw new RuntimeException("手机号和邮箱至少填写一个");
        }

        // 查找用户
        User user = null;
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            user = userRepository.findByPhone(request.getPhone()).orElse(null);
        } else if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user = userRepository.findByEmail(request.getEmail()).orElse(null);
        }

        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证码校验（简化处理：实际项目需要对接短信/邮箱服务）
        if (!"123456".equals(request.getVerifyCode())) {
            throw new RuntimeException("验证码错误");
        }

        // 修改密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
