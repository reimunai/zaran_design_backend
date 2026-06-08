package com.example.zaran_design_backend.service;

import com.example.zaran_design_backend.dto.*;
import com.example.zaran_design_backend.dto.PatternDetailResponse.AuthorBrief;
import com.example.zaran_design_backend.dto.PatternSquareItem.SquareStats;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 图案作品模块（第6模块）业务逻辑。
 *
 * <p>涵盖：个人作品管理（CRUD/二次编辑/软删除恢复）与作品广场（瀑布流/搜索/点赞/收藏/评论/举报）。</p>
 */
@Service
public class PatternService {

    private final PatternRepository patternRepository;
    private final PatternLikeRepository likeRepository;
    private final PatternFavoriteRepository favoriteRepository;
    private final PatternCommentRepository commentRepository;
    private final PatternReportRepository reportRepository;
    private final PatternEditRepository editRepository;
    private final UserRepository userRepository;

    public PatternService(PatternRepository patternRepository,
                         PatternLikeRepository likeRepository,
                         PatternFavoriteRepository favoriteRepository,
                         PatternCommentRepository commentRepository,
                         PatternReportRepository reportRepository,
                         PatternEditRepository editRepository,
                         UserRepository userRepository) {
        this.patternRepository = patternRepository;
        this.likeRepository = likeRepository;
        this.favoriteRepository = favoriteRepository;
        this.commentRepository = commentRepository;
        this.reportRepository = reportRepository;
        this.editRepository = editRepository;
        this.userRepository = userRepository;
    }

    // ========================================================================
    // 6.1 个人作品管理
    // ========================================================================

    // ============================ 6.3.1 从生成结果创建作品 ============================

    @Transactional
    public PatternDetailResponse createPattern(Integer userId, CreatePatternRequest request) {
        Pattern pattern = new Pattern();
        pattern.setUserId(userId);
        pattern.setName(request.getName());
        pattern.setDescription(request.getDescription());
        pattern.setGenerationResultId(request.getGenerationResultId() != null
                ? request.getGenerationResultId().intValue() : null);
        pattern.setTaskId(request.getTaskId());
        pattern.setKValue(request.getKValue());
        pattern.setNoiseLevel(request.getNoiseLevel());
        pattern.setPatchMode(request.getPatchMode());
        pattern.setSketchId(request.getSketchId());
        pattern.setImageUrl(request.getImageUrl());
        pattern.setThumbnailUrl(request.getThumbnailUrl());
        pattern.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : false);
        pattern.setCategory(request.getCategory());
        pattern.setViewCount(0);
        pattern.setLikeCount(0);
        pattern.setFavoriteCount(0);
        pattern.setCommentCount(0);

        if (request.getTags() != null && !request.getTags().isEmpty()) {
            pattern.setTags(String.join(",", request.getTags()));
        }

        patternRepository.save(pattern);

        // 构建详情响应（创建者必然是 owner）
        AuthorBrief author = buildAuthorBrief(userId);
        PatternDetailResponse response = PatternDetailResponse.of(pattern, author, true, false, false);
        response.buildVersionTree(pattern, Collections.emptyList());
        return response;
    }

    // ============================ 6.3.2 我的作品列表 ============================

    public PageResponse<PatternSquareItem> listMyPatterns(Integer userId, int page, int size,
                                                          String keyword, String sort) {
        Pageable pageable = PageRequest.of(page - 1, size, parseSquareSort(sort));

        Specification<Pattern> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), userId));
            predicates.add(cb.isNull(root.get("deletedAt")));
            if (keyword != null && !keyword.isBlank()) {
                String kw = "%" + keyword + "%";
                predicates.add(cb.or(
                        cb.like(root.get("name"), kw),
                        cb.like(root.get("tags"), kw)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Pattern> pageResult = patternRepository.findAll(spec, pageable);
        Map<Integer, AuthorBrief> authorCache = new HashMap<>();
        return PageResponse.of(pageResult, page, size,
                p -> PatternSquareItem.of(p, getCachedAuthor(p.getUserId(), authorCache)));
    }

    // ============================ 6.3.3 作品详情 ============================

    @Transactional
    public PatternDetailResponse getPatternDetail(Integer patternId, Integer currentUserId) {
        Pattern pattern = patternRepository.findByPatternIdAndDeletedAtIsNull(patternId)
                .orElseThrow(() -> new BusinessException(4044, "作品不存在或已删除"));

        // 非公开作品仅所有者或管理员可访问
        boolean isOwner = currentUserId != null && pattern.getUserId().equals(currentUserId);
        boolean isAdmin = currentUserId != null && isCurrentUserAdmin(currentUserId);
        if (!pattern.getIsPublic() && !isOwner && !isAdmin) {
            throw new BusinessException(403, "无权访问他人私密作品");
        }

        // 浏览量 +1
        pattern.setViewCount(pattern.getViewCount() + 1);
        patternRepository.save(pattern);

        // 当前用户的交互状态
        Boolean isLiked = currentUserId != null
                ? likeRepository.existsByPatternIdAndUserId(patternId, currentUserId) : false;
        Boolean isFavorited = currentUserId != null
                ? favoriteRepository.existsByPatternIdAndUserId(patternId, currentUserId) : false;

        AuthorBrief author = buildAuthorBrief(pattern.getUserId());
        PatternDetailResponse response = PatternDetailResponse.of(pattern, author, isOwner, isLiked, isFavorited);

        // 构建版本树
        List<PatternEdit> edits = editRepository.findByPatternIdOrderByCreatedAtAsc(patternId);
        response.buildVersionTree(pattern, edits);
        return response;
    }

    // ============================ 6.3.4 编辑作品信息 ============================

    @Transactional
    public PatternDetailResponse updatePattern(Integer patternId, Integer userId, UpdatePatternRequest request) {
        Pattern pattern = requireOwnedPattern(patternId, userId);

        if (request.getName() != null && !request.getName().isBlank()) {
            pattern.setName(request.getName());
        }
        if (request.getDescription() != null) {
            pattern.setDescription(request.getDescription());
        }
        if (request.getTags() != null) {
            pattern.setTags(String.join(",", request.getTags()));
        }
        if (request.getIsPublic() != null) {
            pattern.setIsPublic(request.getIsPublic());
        }
        if (request.getCategory() != null) {
            pattern.setCategory(request.getCategory());
        }

        patternRepository.save(pattern);

        AuthorBrief author = buildAuthorBrief(userId);
        List<PatternEdit> edits = editRepository.findByPatternIdOrderByCreatedAtAsc(patternId);
        PatternDetailResponse response = PatternDetailResponse.of(pattern, author, true, false, false);
        response.buildVersionTree(pattern, edits);
        return response;
    }

    // ============================ 6.3.5 二次编辑图案 ============================

    @Transactional
    public PatternDetailResponse editPattern(Integer patternId, Integer userId, EditPatternRequest request) {
        Pattern pattern = requireOwnedPattern(patternId, userId);

        // 创建编辑记录
        PatternEdit edit = new PatternEdit();
        edit.setPatternId(patternId);
        edit.setUserId(userId);
        edit.setEditDesc(request.getEditDesc());
        if (request.getImageBase64() != null && !request.getImageBase64().isBlank()) {
            edit.setImageUrl(request.getImageBase64());
            // 更新作品当前图片为编辑后的图片
            pattern.setImageUrl(request.getImageBase64());
        }
        if (request.getThumbnailBase64() != null && !request.getThumbnailBase64().isBlank()) {
            pattern.setThumbnailUrl(request.getThumbnailBase64());
        }
        editRepository.save(edit);
        patternRepository.save(pattern);

        AuthorBrief author = buildAuthorBrief(userId);
        List<PatternEdit> edits = editRepository.findByPatternIdOrderByCreatedAtAsc(patternId);
        PatternDetailResponse response = PatternDetailResponse.of(pattern, author, true, false, false);
        response.buildVersionTree(pattern, edits);
        return response;
    }

    // ============================ 6.3.6 删除作品（软删除） ============================

    @Transactional
    public LocalDateTime softDeletePattern(Integer patternId, Integer userId) {
        Pattern pattern = requireOwnedPattern(patternId, userId);
        pattern.setDeletedAt(LocalDateTime.now());
        patternRepository.save(pattern);
        return pattern.getDeletedAt();
    }

    // ============================ 6.3.7 恢复已删除作品 ============================

    @Transactional
    public Pattern recoverPattern(Integer patternId, Integer userId) {
        Pattern pattern = patternRepository.findById(patternId)
                .orElseThrow(() -> new BusinessException(4044, "作品不存在"));
        if (!pattern.getUserId().equals(userId)) {
            throw new BusinessException(4031, "非作品所有者，无法恢复");
        }
        pattern.setDeletedAt(null);
        patternRepository.save(pattern);
        return pattern;
    }

    // ========================================================================
    // 6.2 作品广场
    // ========================================================================

    // ============================ 6.3.8 作品广场瀑布流 ============================

    public PageResponse<PatternSquareItem> getSquare(int page, int size, String sort,
                                                     String tag, String category) {
        Pageable pageable = PageRequest.of(page - 1, size, parseSquareSort(sort));
        Page<Pattern> pageResult;

        if (tag != null && !tag.isBlank()) {
            // 按标签筛选
            pageResult = patternRepository.findAll(
                    buildSquareSpec(tag, category), pageable);
        } else if (category != null && !category.isBlank()) {
            pageResult = patternRepository.findAll(
                    buildSquareSpec(null, category), pageable);
        } else if ("hot".equalsIgnoreCase(sort)) {
            pageResult = patternRepository.findPublicByHot(pageable);
        } else if ("favorite".equalsIgnoreCase(sort)) {
            pageResult = patternRepository.findPublicByFavorite(pageable);
        } else {
            // 默认最新
            pageResult = patternRepository.findPublicByNewest(pageable);
        }

        Map<Integer, AuthorBrief> authorCache = new HashMap<>();
        return PageResponse.of(pageResult, page, size,
                p -> PatternSquareItem.of(p, getCachedAuthor(p.getUserId(), authorCache)));
    }

    private Specification<Pattern> buildSquareSpec(String tag, String category) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("isPublic")));
            predicates.add(cb.isNull(root.get("deletedAt")));
            if (tag != null && !tag.isBlank()) {
                predicates.add(cb.like(root.get("tags"), "%" + tag + "%"));
            }
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(root.get("category"), category));
            }
            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // ============================ 6.3.9 搜索作品 ============================

    public PageResponse<PatternSquareItem> searchPatterns(String keyword, int page, int size,
                                                          String filtersJson) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 若有关键词，做模糊搜索；否则返回全部公开作品
        Page<Pattern> pageResult;
        if (keyword != null && !keyword.isBlank()) {
            pageResult = patternRepository.searchPublic(keyword, pageable);
        } else {
            pageResult = patternRepository.findByIsPublicTrueAndDeletedAtIsNull(pageable);
        }

        Map<Integer, AuthorBrief> authorCache = new HashMap<>();
        return PageResponse.of(pageResult, page, size,
                p -> PatternSquareItem.of(p, getCachedAuthor(p.getUserId(), authorCache)));
    }

    // ============================ 6.3.10 点赞 ============================

    @Transactional
    public Map<String, Object> likePattern(Integer patternId, Integer userId) {
        Pattern pattern = patternRepository.findByPatternIdAndDeletedAtIsNull(patternId)
                .orElseThrow(() -> new BusinessException(4044, "作品不存在或已删除"));

        if (likeRepository.existsByPatternIdAndUserId(patternId, userId)) {
            throw new BusinessException(409, "已点赞，不可重复点赞");
        }

        PatternLike like = new PatternLike();
        like.setPatternId(patternId);
        like.setUserId(userId);
        likeRepository.save(like);

        pattern.setLikeCount((int) likeRepository.countByPatternId(patternId));
        patternRepository.save(pattern);

        Map<String, Object> data = new HashMap<>();
        data.put("patternId", patternId);
        data.put("likeCount", pattern.getLikeCount());
        return data;
    }

    // ============================ 6.3.11 取消点赞 ============================

    @Transactional
    public Map<String, Object> unlikePattern(Integer patternId, Integer userId) {
        Pattern pattern = patternRepository.findByPatternIdAndDeletedAtIsNull(patternId)
                .orElseThrow(() -> new BusinessException(4044, "作品不存在或已删除"));

        PatternLike like = likeRepository.findByPatternIdAndUserId(patternId, userId)
                .orElseThrow(() -> new BusinessException(404, "未点赞"));

        likeRepository.delete(like);

        pattern.setLikeCount((int) likeRepository.countByPatternId(patternId));
        patternRepository.save(pattern);

        Map<String, Object> data = new HashMap<>();
        data.put("patternId", patternId);
        data.put("likeCount", pattern.getLikeCount());
        return data;
    }

    // ============================ 6.3.12 收藏 ============================

    @Transactional
    public Map<String, Object> favoritePattern(Integer patternId, Integer userId) {
        Pattern pattern = patternRepository.findByPatternIdAndDeletedAtIsNull(patternId)
                .orElseThrow(() -> new BusinessException(4044, "作品不存在或已删除"));

        if (favoriteRepository.existsByPatternIdAndUserId(patternId, userId)) {
            throw new BusinessException(409, "已收藏，不可重复收藏");
        }

        PatternFavorite favorite = new PatternFavorite();
        favorite.setPatternId(patternId);
        favorite.setUserId(userId);
        favoriteRepository.save(favorite);

        pattern.setFavoriteCount((int) favoriteRepository.countByPatternId(patternId));
        patternRepository.save(pattern);

        Map<String, Object> data = new HashMap<>();
        data.put("patternId", patternId);
        data.put("favoriteCount", pattern.getFavoriteCount());
        return data;
    }

    // ============================ 6.3.13 取消收藏 ============================

    @Transactional
    public Map<String, Object> unfavoritePattern(Integer patternId, Integer userId) {
        Pattern pattern = patternRepository.findByPatternIdAndDeletedAtIsNull(patternId)
                .orElseThrow(() -> new BusinessException(4044, "作品不存在或已删除"));

        PatternFavorite favorite = favoriteRepository.findByPatternIdAndUserId(patternId, userId)
                .orElseThrow(() -> new BusinessException(404, "未收藏"));

        favoriteRepository.delete(favorite);

        pattern.setFavoriteCount((int) favoriteRepository.countByPatternId(patternId));
        patternRepository.save(pattern);

        Map<String, Object> data = new HashMap<>();
        data.put("patternId", patternId);
        data.put("favoriteCount", pattern.getFavoriteCount());
        return data;
    }

    // ============================ 6.3.14 获取评论列表 ============================

    public PageResponse<CommentResponse> getComments(Integer patternId, int page, int size) {
        // 确认作品存在且未删除
        patternRepository.findByPatternIdAndDeletedAtIsNull(patternId)
                .orElseThrow(() -> new BusinessException(4044, "作品不存在或已删除"));

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PatternComment> pageResult = commentRepository
                .findByPatternIdAndParentIdIsNullOrderByCreatedAtDesc(patternId, pageable);

        Map<Integer, AuthorBrief> authorCache = new HashMap<>();

        List<CommentResponse> list = pageResult.getContent().stream()
                .map(comment -> {
                    AuthorBrief author = getCachedAuthor(comment.getUserId(), authorCache);
                    // 查询子回复
                    List<PatternComment> childComments = commentRepository
                            .findByParentIdOrderByCreatedAtAsc(comment.getCommentId());
                    List<CommentResponse> replies = childComments.stream()
                            .map(child -> CommentResponse.of(child,
                                    getCachedAuthor(child.getUserId(), authorCache), null))
                            .collect(Collectors.toList());
                    return CommentResponse.of(comment, author, replies);
                })
                .collect(Collectors.toList());

        PageResponse<CommentResponse> response = new PageResponse<>();
        response.setList(list);
        response.setTotal(pageResult.getTotalElements());
        response.setPage(page);
        response.setSize(size);
        response.setPages(pageResult.getTotalPages());
        return response;
    }

    // ============================ 6.3.15 发表评论 ============================

    @Transactional
    public CommentResponse createComment(Integer patternId, Integer userId, CreateCommentRequest request) {
        Pattern pattern = patternRepository.findByPatternIdAndDeletedAtIsNull(patternId)
                .orElseThrow(() -> new BusinessException(4044, "作品不存在或已删除"));

        PatternComment comment = new PatternComment();
        comment.setPatternId(patternId);
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        if (request.getParentId() != null) {
            comment.setParentId(request.getParentId().intValue());
            // 验证父评论存在且属于同一作品
            commentRepository.findById(request.getParentId().intValue())
                    .orElseThrow(() -> new BusinessException(404, "父评论不存在"));
        }

        commentRepository.save(comment);

        // 更新作品评论计数
        pattern.setCommentCount((int) commentRepository.countByPatternId(patternId));
        patternRepository.save(pattern);

        AuthorBrief author = buildAuthorBrief(userId);
        return CommentResponse.of(comment, author, null);
    }

    // ============================ 6.3.16 删除评论 ============================

    @Transactional
    public void deleteComment(Integer patternId, Integer commentId, Integer userId, String role) {
        PatternComment comment = commentRepository.findByCommentIdAndPatternId(commentId, patternId)
                .orElseThrow(() -> new BusinessException(404, "评论不存在"));

        // 仅评论作者或管理员可删除
        boolean isAuthor = comment.getUserId().equals(userId);
        boolean isAdmin = "admin".equalsIgnoreCase(role);
        if (!isAuthor && !isAdmin) {
            throw new BusinessException(403, "无权删除他人评论");
        }

        commentRepository.delete(comment);

        // 更新作品评论计数
        Pattern pattern = patternRepository.findByPatternIdAndDeletedAtIsNull(patternId)
                .orElse(null);
        if (pattern != null) {
            pattern.setCommentCount((int) commentRepository.countByPatternId(patternId));
            patternRepository.save(pattern);
        }
    }

    // ============================ 6.3.17 举报作品 ============================

    @Transactional
    public Map<String, Object> reportPattern(Integer patternId, Integer userId, ReportRequest request) {
        Pattern pattern = patternRepository.findByPatternIdAndDeletedAtIsNull(patternId)
                .orElseThrow(() -> new BusinessException(4044, "作品不存在或已删除"));

        if (reportRepository.existsByPatternIdAndUserId(patternId, userId)) {
            throw new BusinessException(409, "已举报过该作品，不可重复举报");
        }

        PatternReport report = new PatternReport();
        report.setPatternId(patternId);
        report.setUserId(userId);
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        reportRepository.save(report);

        Map<String, Object> data = new HashMap<>();
        data.put("reportId", report.getReportId());
        data.put("patternId", patternId);
        data.put("status", report.getStatus());
        data.put("createdAt", report.getCreatedAt());
        return data;
    }

    // ========================================================================
    // 辅助方法
    // ========================================================================

    /** 要求作品存在、未删除且属于当前用户 */
    private Pattern requireOwnedPattern(Integer patternId, Integer userId) {
        Pattern pattern = patternRepository.findByPatternIdAndDeletedAtIsNull(patternId)
                .orElseThrow(() -> new BusinessException(4044, "作品不存在或已删除"));
        if (!pattern.getUserId().equals(userId)) {
            throw new BusinessException(4031, "非作品所有者，无法操作");
        }
        return pattern;
    }

    /** 构建作者简要信息 */
    private AuthorBrief buildAuthorBrief(Integer userId) {
        return userRepository.findById(userId)
                .map(u -> new AuthorBrief(u.getUserId(), u.getUsername(),
                        u.getAvatar(), u.getRole().name()))
                .orElse(new AuthorBrief(userId, "未知用户", null, "tourist"));
    }

    /** 带缓存的作者信息获取 */
    private AuthorBrief getCachedAuthor(Integer userId, Map<Integer, AuthorBrief> cache) {
        return cache.computeIfAbsent(userId, this::buildAuthorBrief);
    }

    /** 判断当前用户是否为管理员 */
    private boolean isCurrentUserAdmin(Integer userId) {
        return userRepository.findById(userId)
                .map(u -> u.getRole() == User.Role.admin)
                .orElse(false);
    }

    /** 解析广场排序参数 */
    private Sort parseSquareSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return switch (sort.toLowerCase()) {
            case "hot" -> Sort.by(Sort.Direction.DESC, "likeCount");
            case "favorite" -> Sort.by(Sort.Direction.DESC, "favoriteCount");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}
