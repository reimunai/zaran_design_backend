package com.example.zaran_design_backend.service;

import com.example.zaran_design_backend.dto.*;
import com.example.zaran_design_backend.dto.SketchListItem.CategoryBrief;
import com.example.zaran_design_backend.entity.Sketch;
import com.example.zaran_design_backend.entity.SketchCategory;
import com.example.zaran_design_backend.entity.SketchVersion;
import com.example.zaran_design_backend.repository.SketchCategoryRepository;
import com.example.zaran_design_backend.repository.SketchRepository;
import com.example.zaran_design_backend.repository.SketchVersionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 草图绘制模块业务逻辑。
 *
 * <p>实现说明（与文档的环境差异）：</p>
 * <ul>
 *   <li>本项目使用 Spring Data JPA（非文档中的 MyBatis-Plus），三张表 sketches /
 *       sketch_versions / sketch_categories 由 JPA 实体在 ddl-auto=update 下自动建表。</li>
 *   <li>项目未集成 MinIO，缩略图不上传对象存储，而是直接将前端传入的 imageBase64
 *       （data URL）作为 thumbnailPath 存储；前端可直接渲染。</li>
 *   <li>layersJson 在库中以 JSON 字符串（LONGTEXT）存储，出入参均为 JsonNode 对象。</li>
 * </ul>
 */
@Service
public class SketchService {

    private final SketchRepository sketchRepository;
    private final SketchVersionRepository versionRepository;
    private final SketchCategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;

    public SketchService(SketchRepository sketchRepository,
                         SketchVersionRepository versionRepository,
                         SketchCategoryRepository categoryRepository,
                         ObjectMapper objectMapper) {
        this.sketchRepository = sketchRepository;
        this.versionRepository = versionRepository;
        this.categoryRepository = categoryRepository;
        this.objectMapper = objectMapper;
    }

    // ============================ 4.2.1 创建新草图 ============================

    @Transactional
    public SketchResponse createSketch(Integer userId, CreateSketchRequest request) {
        // categoryId 校验（文档错误码 404）
        if (request.getCategoryId() != null && !categoryRepository.existsById(request.getCategoryId())) {
            throw new BusinessException(404, "categoryId 对应分类不存在");
        }

        Sketch sketch = new Sketch();
        sketch.setUserId(userId);
        sketch.setName(request.getName() != null && !request.getName().isBlank()
                ? request.getName() : "未命名草图");
        sketch.setWidth(request.getWidth());
        sketch.setHeight(request.getHeight());
        sketch.setCategoryId(request.getCategoryId());
        sketch.setLayersJson(null); // 初始为空
        sketch.setIsPublic(false);
        sketch.setCurrentVersion(0);

        sketchRepository.save(sketch);
        return SketchResponse.of(sketch);
    }

    // ============================ 4.2.2 查询我的草图列表 ============================

    public PageResponse<SketchListItem> listMySketches(Integer userId, int page, int size,
                                                       String keyword, Integer categoryId,
                                                       Boolean isPublic, String sort,
                                                       boolean includeDeleted) {
        Pageable pageable = PageRequest.of(page - 1, size, parseSort(sort));

        Specification<Sketch> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("userId"), userId));
            if (!includeDeleted) {
                predicates.add(cb.isNull(root.get("deletedAt")));
            }
            if (keyword != null && !keyword.isBlank()) {
                predicates.add(cb.like(root.get("name"), "%" + keyword + "%"));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
            }
            if (isPublic != null) {
                predicates.add(cb.equal(root.get("isPublic"), isPublic));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        var pageResult = sketchRepository.findAll(spec, pageable);

        // 预取分类名，避免 N+1
        Map<Integer, String> categoryNames = loadCategoryNames();

        return PageResponse.of(pageResult, page, size, sketch -> {
            CategoryBrief brief = null;
            if (sketch.getCategoryId() != null) {
                brief = new CategoryBrief(sketch.getCategoryId(),
                        categoryNames.get(sketch.getCategoryId()));
            }
            return SketchListItem.of(sketch, brief);
        });
    }

    // ============================ 4.2.3 获取草图详情 ============================

    public SketchDetailResponse getSketchDetail(Integer sketchId, Integer currentUserId, String currentRole) {
        Sketch sketch = sketchRepository.findBySketchIdAndDeletedAtIsNull(sketchId)
                .orElseThrow(() -> new BusinessException(4042, "草图不存在或已删除"));

        boolean isOwner = sketch.getUserId().equals(currentUserId);
        boolean isAdmin = "admin".equalsIgnoreCase(currentRole);

        // 私密草图：仅所有者或管理员可访问；公开草图任意登录用户可看
        if (!sketch.getIsPublic() && !isOwner && !isAdmin) {
            throw new BusinessException(403, "无权访问他人私密草图");
        }

        SketchDetailResponse response = new SketchDetailResponse();
        response.setSketchId(sketch.getSketchId());
        response.setName(sketch.getName());
        response.setThumbnailUrl(sketch.getThumbnailPath());
        response.setLayersJson(parseJson(sketch.getLayersJson()));
        response.setWidth(sketch.getWidth());
        response.setHeight(sketch.getHeight());
        response.setIsPublic(sketch.getIsPublic());
        response.setIsOwner(isOwner);
        response.setCurrentVersion(sketch.getCurrentVersion());
        response.setCategory(buildCategoryBrief(sketch.getCategoryId()));
        response.setTags(splitTags(sketch.getTags()));
        response.setCreatedAt(sketch.getCreatedAt());
        response.setUpdatedAt(sketch.getUpdatedAt());
        return response;
    }

    // ============================ 4.2.4 保存草图 ============================

    @Transactional
    public SketchDetailResponse saveSketch(Integer sketchId, Integer userId, SaveSketchRequest request) {
        Sketch sketch = sketchRepository.findBySketchIdAndDeletedAtIsNull(sketchId)
                .orElseThrow(() -> new BusinessException(4042, "草图不存在或已删除"));

        if (!sketch.getUserId().equals(userId)) {
            throw new BusinessException(4031, "非草图所有者，无法保存");
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            sketch.setName(request.getName());
        }
        sketch.setLayersJson(writeJson(request.getLayersJson()));
        if (request.getCategoryId() != null) {
            sketch.setCategoryId(request.getCategoryId());
        }
        if (request.getTags() != null) {
            sketch.setTags(String.join(",", request.getTags()));
        }
        // 缩略图：有 imageBase64 时更新（无 MinIO，直接存 data URL）
        if (request.getImageBase64() != null && !request.getImageBase64().isBlank()) {
            sketch.setThumbnailPath(request.getImageBase64());
        }

        sketchRepository.save(sketch);

        // 复用详情构建（保存者即所有者）
        SketchDetailResponse response = new SketchDetailResponse();
        response.setSketchId(sketch.getSketchId());
        response.setName(sketch.getName());
        response.setThumbnailUrl(sketch.getThumbnailPath());
        response.setLayersJson(parseJson(sketch.getLayersJson()));
        response.setWidth(sketch.getWidth());
        response.setHeight(sketch.getHeight());
        response.setIsPublic(sketch.getIsPublic());
        response.setIsOwner(true);
        response.setCurrentVersion(sketch.getCurrentVersion());
        response.setCategory(buildCategoryBrief(sketch.getCategoryId()));
        response.setTags(splitTags(sketch.getTags()));
        response.setCreatedAt(sketch.getCreatedAt());
        response.setUpdatedAt(sketch.getUpdatedAt());
        return response;
    }

    // ============================ 4.2.5 设置公开/私密 ============================

    @Transactional
    public Sketch setVisibility(Integer sketchId, Integer userId, boolean isPublic) {
        Sketch sketch = requireOwnedSketch(sketchId, userId);
        sketch.setIsPublic(isPublic);
        sketchRepository.save(sketch);
        return sketch;
    }

    // ============================ 4.2.6 删除草图（软删除） ============================

    @Transactional
    public LocalDateTime softDelete(Integer sketchId, Integer userId) {
        Sketch sketch = requireOwnedSketch(sketchId, userId);
        sketch.setDeletedAt(LocalDateTime.now());
        sketchRepository.save(sketch);
        return sketch.getDeletedAt();
    }

    // ============================ 4.2.7 恢复已删除草图 ============================

    @Transactional
    public Sketch recover(Integer sketchId, Integer userId) {
        // 恢复需查到已删除记录，故不使用 findBySketchIdAndDeletedAtIsNull
        Sketch sketch = sketchRepository.findById(sketchId)
                .orElseThrow(() -> new BusinessException(4042, "草图不存在"));
        if (!sketch.getUserId().equals(userId)) {
            throw new BusinessException(4031, "非草图所有者，无法恢复");
        }
        sketch.setDeletedAt(null);
        sketchRepository.save(sketch);
        return sketch;
    }

    // ============================ 4.2.8 创建版本快照 ============================

    @Transactional
    public CreateVersionResponse createVersion(Integer sketchId, Integer userId, CreateVersionRequest request) {
        Sketch sketch = requireOwnedSketch(sketchId, userId);

        int nextVersionNo = versionRepository.findTopBySketchIdOrderByVersionNumberDesc(sketchId)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        SketchVersion version = new SketchVersion();
        version.setSketchId(sketchId);
        version.setVersionNumber(nextVersionNo);
        version.setChangeDesc(request.getChangeDesc());
        version.setLayersJson(writeJson(request.getLayersJson()));
        if (request.getImageBase64() != null && !request.getImageBase64().isBlank()) {
            version.setThumbnailPath(request.getImageBase64());
        }
        versionRepository.save(version);

        // 更新主记录当前版本号
        sketch.setCurrentVersion(nextVersionNo);
        sketchRepository.save(sketch);

        return CreateVersionResponse.of(version);
    }

    // ============================ 4.2.9 获取版本历史 ============================

    public PageResponse<VersionResponse> listVersions(Integer sketchId, Integer userId, String currentRole,
                                                      int page, int size) {
        // 需能读取该草图（所有者/管理员/公开）
        requireReadableSketch(sketchId, userId, currentRole);

        Pageable pageable = PageRequest.of(page - 1, size);
        var pageResult = versionRepository.findBySketchIdOrderByVersionNumberDesc(sketchId, pageable);
        return PageResponse.of(pageResult, page, size, VersionResponse::of);
    }

    // ============================ 4.2.10 获取指定版本详情 ============================

    public VersionDetailResponse getVersionDetail(Integer sketchId, Integer versionId,
                                                  Integer userId, String currentRole) {
        requireReadableSketch(sketchId, userId, currentRole);

        SketchVersion version = versionRepository.findByVersionIdAndSketchId(versionId, sketchId)
                .orElseThrow(() -> new BusinessException(404, "版本不存在，或该版本不属于此草图"));

        VersionDetailResponse response = new VersionDetailResponse();
        response.setVersionId(version.getVersionId());
        response.setVersionNo(version.getVersionNumber());
        response.setChangeDesc(version.getChangeDesc());
        response.setThumbnailUrl(version.getThumbnailPath());
        response.setLayersJson(parseJson(version.getLayersJson()));
        response.setCreatedAt(version.getCreatedAt());
        return response;
    }

    // ============================ 4.2.11 回退到指定版本 ============================

    @Transactional
    public RestoreVersionResponse restoreVersion(Integer sketchId, Integer versionId, Integer userId) {
        Sketch sketch = sketchRepository.findBySketchIdAndDeletedAtIsNull(sketchId)
                .orElseThrow(() -> new BusinessException(4042, "草图不存在或已删除"));
        if (!sketch.getUserId().equals(userId)) {
            throw new BusinessException(4031, "非草图所有者，无法回退");
        }

        SketchVersion target = versionRepository.findByVersionIdAndSketchId(versionId, sketchId)
                .orElseThrow(() -> new BusinessException(404, "目标版本不存在"));

        // 以最新版本号+1 生成一条新版本快照，内容为目标版本内容，保证历史可追溯
        int nextVersionNo = versionRepository.findTopBySketchIdOrderByVersionNumberDesc(sketchId)
                .map(v -> v.getVersionNumber() + 1)
                .orElse(1);

        SketchVersion snapshot = new SketchVersion();
        snapshot.setSketchId(sketchId);
        snapshot.setVersionNumber(nextVersionNo);
        snapshot.setChangeDesc("回退到版本 " + target.getVersionNumber());
        snapshot.setLayersJson(target.getLayersJson());
        snapshot.setThumbnailPath(target.getThumbnailPath());
        versionRepository.save(snapshot);

        // 主记录图层更新为目标版本内容
        sketch.setLayersJson(target.getLayersJson());
        sketch.setThumbnailPath(target.getThumbnailPath());
        sketch.setCurrentVersion(nextVersionNo);
        sketchRepository.save(sketch);

        RestoreVersionResponse response = new RestoreVersionResponse();
        response.setSketchId(sketchId);
        response.setCurrentVersion(nextVersionNo);
        response.setRestoredFrom(versionId);
        response.setLayersJson(parseJson(target.getLayersJson()));
        return response;
    }

    // ============================ 4.2.12 导入本地图片 ============================

    @Transactional
    public ImportImageResponse importImage(Integer sketchId, Integer userId, MultipartFile file,
                                           boolean asLayer, String layerName) {
        Sketch sketch = requireOwnedSketch(sketchId, userId);

        // 文件格式校验（PNG/JPG/SVG）
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        boolean validType = isImageContentType(contentType)
                || filename.endsWith(".png") || filename.endsWith(".jpg")
                || filename.endsWith(".jpeg") || filename.endsWith(".svg");
        if (!validType) {
            throw new BusinessException(4002, "文件格式错误（非 PNG/JPG/SVG）");
        }

        // 大小校验（20MB）
        if (file.getSize() > 20L * 1024 * 1024) {
            throw new BusinessException(4003, "文件超过 20MB 限制");
        }

        // 无 MinIO：将图片转为 data URL 存储（占位实现）
        String dataUrl;
        try {
            String base64 = java.util.Base64.getEncoder().encodeToString(file.getBytes());
            String mime = contentType != null ? contentType : "image/png";
            dataUrl = "data:" + mime + ";base64," + base64;
        } catch (Exception e) {
            throw new BusinessException(5002, "文件上传失败");
        }

        String layerId = "layer_import_" + UUID.randomUUID().toString().substring(0, 8);
        String name = (layerName != null && !layerName.isBlank()) ? layerName : "导入图层";

        // 更新 layersJson：asLayer=true 追加新图层；false 替换为单一背景图层
        ObjectMapper m = objectMapper;
        com.fasterxml.jackson.databind.node.ObjectNode newLayer = m.createObjectNode();
        newLayer.put("id", layerId);
        newLayer.put("name", name);
        newLayer.put("type", "raster");
        newLayer.put("visible", true);
        newLayer.put("opacity", 1.0);
        newLayer.put("data", dataUrl);

        JsonNode root = parseJson(sketch.getLayersJson());
        com.fasterxml.jackson.databind.node.ObjectNode rootObj;
        com.fasterxml.jackson.databind.node.ArrayNode layers;
        if (asLayer && root != null && root.isObject() && root.has("layers") && root.get("layers").isArray()) {
            rootObj = (com.fasterxml.jackson.databind.node.ObjectNode) root;
            layers = (com.fasterxml.jackson.databind.node.ArrayNode) rootObj.get("layers");
        } else {
            rootObj = m.createObjectNode();
            rootObj.put("version", "1.0");
            com.fasterxml.jackson.databind.node.ObjectNode canvas = m.createObjectNode();
            canvas.put("width", sketch.getWidth());
            canvas.put("height", sketch.getHeight());
            rootObj.set("canvas", canvas);
            layers = m.createArrayNode();
            rootObj.set("layers", layers);
        }
        layers.add(newLayer);
        sketch.setLayersJson(writeJson(rootObj));
        sketchRepository.save(sketch);

        return new ImportImageResponse(layerId, dataUrl, sketch.getWidth(), sketch.getHeight());
    }

    // ============================ 4.2.13 获取草图分类树 ============================

    public List<CategoryNode> getCategoryTree() {
        List<SketchCategory> all = categoryRepository.findAllByOrderBySortOrderAsc();

        Map<Integer, CategoryNode> nodeMap = new LinkedHashMap<>();
        for (SketchCategory c : all) {
            nodeMap.put(c.getCategoryId(), CategoryNode.of(c));
        }

        List<CategoryNode> roots = new ArrayList<>();
        for (SketchCategory c : all) {
            CategoryNode node = nodeMap.get(c.getCategoryId());
            if (c.getParentId() == null) {
                roots.add(node);
            } else {
                CategoryNode parent = nodeMap.get(c.getParentId());
                if (parent != null) {
                    parent.getChildren().add(node);
                } else {
                    roots.add(node); // 父节点缺失则当作根节点
                }
            }
        }
        return roots;
    }

    // ============================ 4.2.14 新增草图分类 ============================

    /**
     * 创建草图分类。仅传承人（inheritor）和管理员（admin）可操作。
     * 若指定 parentId，则父分类必须存在。
     */
    @Transactional
    public CategoryNode createCategory(CreateCategoryRequest request, String currentRole) {
        if (!"inheritor".equalsIgnoreCase(currentRole) && !"admin".equalsIgnoreCase(currentRole)) {
            throw new BusinessException(403, "无权创建草图分类，仅传承人或管理员可操作");
        }

        if (request.getParentId() != null && !categoryRepository.existsById(request.getParentId())) {
            throw new BusinessException(404, "parentId 对应父分类不存在");
        }

        SketchCategory category = new SketchCategory();
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setParentId(request.getParentId());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        categoryRepository.save(category);
        return CategoryNode.of(category);
    }

    // ============================ 辅助方法 ============================

    /** 要求草图存在、未删除且属于当前用户，否则抛业务异常 */
    private Sketch requireOwnedSketch(Integer sketchId, Integer userId) {
        Sketch sketch = sketchRepository.findBySketchIdAndDeletedAtIsNull(sketchId)
                .orElseThrow(() -> new BusinessException(4042, "草图不存在或已删除"));
        if (!sketch.getUserId().equals(userId)) {
            throw new BusinessException(4031, "非草图所有者，无法操作");
        }
        return sketch;
    }

    /** 要求当前用户可读取该草图（所有者/管理员/公开） */
    private Sketch requireReadableSketch(Integer sketchId, Integer userId, String role) {
        Sketch sketch = sketchRepository.findBySketchIdAndDeletedAtIsNull(sketchId)
                .orElseThrow(() -> new BusinessException(4042, "草图不存在或已删除"));
        boolean isOwner = sketch.getUserId().equals(userId);
        boolean isAdmin = "admin".equalsIgnoreCase(role);
        if (!sketch.getIsPublic() && !isOwner && !isAdmin) {
            throw new BusinessException(403, "无权访问他人私密草图");
        }
        return sketch;
    }

    private CategoryBrief buildCategoryBrief(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .map(c -> new CategoryBrief(c.getCategoryId(), c.getName()))
                .orElse(new CategoryBrief(categoryId, null));
    }

    private Map<Integer, String> loadCategoryNames() {
        Map<Integer, String> map = new LinkedHashMap<>();
        for (SketchCategory c : categoryRepository.findAll()) {
            map.put(c.getCategoryId(), c.getName());
        }
        return map;
    }

    private List<String> splitTags(String tags) {
        if (tags == null || tags.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /** 解析排序参数，如 "updatedAt,desc"；默认 updatedAt desc */
    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "updatedAt");
        }
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        // 仅允许已知字段，避免非法属性导致异常
        if (!field.equals("createdAt") && !field.equals("updatedAt")) {
            field = "updatedAt";
        }
        Sort.Direction direction = (parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }

    private JsonNode parseJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String writeJson(JsonNode node) {
        if (node == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new BusinessException(500, "图层数据序列化失败");
        }
    }

    private boolean isImageContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        return contentType.equals("image/png")
                || contentType.equals("image/jpeg")
                || contentType.equals("image/jpg")
                || contentType.equals("image/svg+xml");
    }
}
