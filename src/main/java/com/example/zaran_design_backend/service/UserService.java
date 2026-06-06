package com.example.zaran_design_backend.service;

import com.example.zaran_design_backend.dto.*;
import com.example.zaran_design_backend.entity.User;
import com.example.zaran_design_backend.entity.UserCertificationRequest;
import com.example.zaran_design_backend.entity.UserFollow;
import com.example.zaran_design_backend.repository.UserCertificationRequestRepository;
import com.example.zaran_design_backend.repository.UserFollowRepository;
import com.example.zaran_design_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final UserCertificationRequestRepository certificationRequestRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserFollowRepository userFollowRepository,
                       UserCertificationRequestRepository certificationRequestRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userFollowRepository = userFollowRepository;
        this.certificationRequestRepository = certificationRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 获取当前用户信息
     */
    public UserProfileResponse getProfile(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        long followerCount = userFollowRepository.countByFollowingId(userId);
        long followingCount = userFollowRepository.countByFollowerId(userId);

        return UserProfileResponse.of(user, 0, 0, followerCount, followingCount);
    }

    /**
     * 修改个人信息
     */
    public UserProfileResponse updateProfile(Integer userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (request.getAvatar() != null) user.setAvatar(request.getAvatar());
        if (request.getBio() != null) user.setBio(request.getBio());
        if (request.getProfessionField() != null) user.setProfessionalField(request.getProfessionField());
        if (request.getPortfolioUrl() != null) user.setPortfolioUrl(request.getPortfolioUrl());

        userRepository.save(user);

        long followerCount = userFollowRepository.countByFollowingId(userId);
        long followingCount = userFollowRepository.countByFollowerId(userId);

        return UserProfileResponse.of(user, 0, 0, followerCount, followingCount);
    }

    /**
     * 修改密码
     */
    public void changePassword(Integer userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * 申请成为设计师
     */
    public ApplicationResponse applyDesigner(Integer userId, DesignerApplyRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 只有游客可以申请
        if (user.getRole() != User.Role.tourist) {
            throw new RuntimeException("当前角色无法申请成为设计师");
        }

        // 检查是否有待审核的申请
        List<UserCertificationRequest> existingRequests = certificationRequestRepository.findByUserId(userId);
        boolean hasPending = existingRequests.stream()
                .anyMatch(r -> r.getStatus() == UserCertificationRequest.CertificationStatus.pending
                        && r.getTargetRole() == UserCertificationRequest.TargetRole.designer);
        if (hasPending) {
            throw new RuntimeException("您已有待审核的设计师申请，请耐心等待");
        }

        UserCertificationRequest certRequest = new UserCertificationRequest();
        certRequest.setUserId(userId);
        certRequest.setTargetRole(UserCertificationRequest.TargetRole.designer);
        certRequest.setReason(request.getReason());
        certRequest.setAttachments(request.getPortfolioUrl()); // 作品集链接存在attachments字段
        certRequest.setStatus(UserCertificationRequest.CertificationStatus.pending);

        certificationRequestRepository.save(certRequest);

        return ApplicationResponse.of(certRequest, user);
    }

    /**
     * 查看认证申请列表
     */
    public List<ApplicationResponse> getApplications(String role) {
        // 只有传承人和管理员可以查看
        if (!"inheritor".equals(role) && !"admin".equals(role)) {
            throw new RuntimeException("无权查看认证申请列表");
        }

        List<UserCertificationRequest> requests = certificationRequestRepository.findByStatus(
                UserCertificationRequest.CertificationStatus.pending);

        return requests.stream().map(req -> {
            User user = userRepository.findById(req.getUserId()).orElse(null);
            return ApplicationResponse.of(req, user != null ? user : new User());
        }).collect(Collectors.toList());
    }

    /**
     * 审核通过设计师申请
     */
    public ApplicationResponse approveApplication(Integer requestId, Integer reviewerId, ApplicationReviewRequest reviewRequest) {
        UserCertificationRequest certRequest = certificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("申请不存在"));

        if (certRequest.getStatus() != UserCertificationRequest.CertificationStatus.pending) {
            throw new RuntimeException("该申请已处理");
        }

        // 更新申请状态
        certRequest.setStatus(UserCertificationRequest.CertificationStatus.approved);
        certRequest.setReviewerId(reviewerId);
        certRequest.setReviewComment(reviewRequest.getReviewerComment());
        certRequest.setReviewedAt(LocalDateTime.now());
        certificationRequestRepository.save(certRequest);

        // 更新用户角色
        User user = userRepository.findById(certRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setRole(User.Role.designer);
        user.setCertificationStatus(User.CertificationStatus.approved);
        user.setCertifiedBy(reviewerId);
        userRepository.save(user);

        return ApplicationResponse.of(certRequest, user);
    }

    /**
     * 拒绝设计师申请
     */
    public ApplicationResponse rejectApplication(Integer requestId, Integer reviewerId, ApplicationReviewRequest reviewRequest) {
        UserCertificationRequest certRequest = certificationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("申请不存在"));

        if (certRequest.getStatus() != UserCertificationRequest.CertificationStatus.pending) {
            throw new RuntimeException("该申请已处理");
        }

        certRequest.setStatus(UserCertificationRequest.CertificationStatus.rejected);
        certRequest.setReviewerId(reviewerId);
        certRequest.setReviewComment(reviewRequest.getReviewerComment());
        certRequest.setReviewedAt(LocalDateTime.now());
        certificationRequestRepository.save(certRequest);

        User user = userRepository.findById(certRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return ApplicationResponse.of(certRequest, user);
    }

    /**
     * 查看指定用户主页
     */
    public UserPublicResponse getUserPublic(Integer targetUserId, Integer currentUserId) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        boolean isFollowing = false;
        if (currentUserId != null) {
            isFollowing = userFollowRepository.existsByFollowerIdAndFollowingId(currentUserId, targetUserId);
        }

        long followerCount = userFollowRepository.countByFollowingId(targetUserId);

        return UserPublicResponse.of(user, isFollowing, 0, followerCount);
    }

    /**
     * 关注用户
     */
    public void followUser(Integer followerId, Integer followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("不能关注自己");
        }

        // 检查目标用户是否存在
        if (!userRepository.existsById(followingId)) {
            throw new RuntimeException("用户不存在");
        }

        // 检查是否已关注
        if (userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new RuntimeException("已关注该用户");
        }

        UserFollow follow = new UserFollow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        userFollowRepository.save(follow);
    }

    /**
     * 取消关注
     */
    public void unfollowUser(Integer followerId, Integer followingId) {
        if (!userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new RuntimeException("未关注该用户");
        }

        userFollowRepository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }
}
