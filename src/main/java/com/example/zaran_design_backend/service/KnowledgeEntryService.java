package com.example.zaran_design_backend.service;

import com.example.zaran_design_backend.dto.KnowledgeEntryRequest;
import com.example.zaran_design_backend.entity.KnowledgeEntry;
import com.example.zaran_design_backend.repository.KnowledgeEntryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KnowledgeEntryService {

    private final KnowledgeEntryRepository knowledgeEntryRepository;

    public KnowledgeEntryService(KnowledgeEntryRepository knowledgeEntryRepository) {
        this.knowledgeEntryRepository = knowledgeEntryRepository;
    }

    public List<KnowledgeEntry> getPublishedEntries() {
        return knowledgeEntryRepository.findByStatusOrderByCreatedAtDesc(KnowledgeEntry.Status.published);
    }

    // ====== ↓↓↓ 这是你新加的方法 ↓↓↓ ======
    public KnowledgeEntry createEntry(KnowledgeEntryRequest request) {
        // 1. 创建一个新的实体对象
        KnowledgeEntry entry = new KnowledgeEntry();

        // 2. 把前端传来的数据装进去
        entry.setTitle(request.getTitle());
        entry.setContent(request.getContent());
        entry.setCategory(KnowledgeEntry.Category.valueOf(request.getCategory()));

        // 3. 处理图片（取第一张作为封面图）
        if (request.getImageAttachments() != null && !request.getImageAttachments().isEmpty()) {
            entry.setCoverImage(request.getImageAttachments().get(0));
        }

        // 4. 设置默认值（根据接口文档，新提交的应该是 pending 状态）
        entry.setStatus(KnowledgeEntry.Status.pending);

        // 注意：因为我们现在还没做JWT登录模块，先硬编码写死一个作者ID（比如 1 号用户 admin_system）
        // 等以后你们组整合了登录功能，这里再换成从 Token 里获取真实用户ID
        entry.setAuthorId(1);

        // 5. 让仓库管理员存进数据库
        return knowledgeEntryRepository.save(entry);
    }
    /**
     * 1. 根据ID获取知识详情
     */
    public KnowledgeEntry getEntryById(Integer entryId) {
        // findById 是 JPA 自带的方法。如果找不到，就抛出一个异常
        return knowledgeEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("错误：未找到该工艺知识条目！"));
    }

    /**
     * 2. 修改现有的知识条目
     */
    public KnowledgeEntry updateEntry(Integer entryId, KnowledgeEntryRequest request) {
        // 先去数据库查出原本的数据
        KnowledgeEntry entry = getEntryById(entryId);

        // 把前端传来的新内容覆盖进去
        entry.setTitle(request.getTitle());
        entry.setContent(request.getContent());
        entry.setCategory(KnowledgeEntry.Category.valueOf(request.getCategory()));

        if (request.getImageAttachments() != null && !request.getImageAttachments().isEmpty()) {
            entry.setCoverImage(request.getImageAttachments().get(0));
        }

        // 修改后，状态重新调整为待审核 pending
        entry.setStatus(KnowledgeEntry.Status.pending);

        // 重新保存，JPA 发现 ID 已存在就会自动执行 UPDATE 语句
        return knowledgeEntryRepository.save(entry);
    }

    /**
     * 3. 删除现有的知识条目
     */
    public void deleteEntry(Integer entryId) {
        // 先确保这个数据确实存在
        KnowledgeEntry entry = getEntryById(entryId);
        // 执行物理删除
        knowledgeEntryRepository.delete(entry);
    }

    /**
     * 获取所有“待审核”的知识列表
     */
    public List<KnowledgeEntry> getPendingEntries() {
        return knowledgeEntryRepository.findByStatusOrderByCreatedAtDesc(KnowledgeEntry.Status.pending);
    }

    /**
     * 审核通过
     */
    public KnowledgeEntry approveEntry(Integer entryId, Integer reviewerId) {
        KnowledgeEntry entry = getEntryById(entryId);

        // 修改状态为 published
        entry.setStatus(KnowledgeEntry.Status.published);
        // 记录是谁审核的
        entry.setReviewerId(reviewerId);
        // 记录发布时间
        entry.setPublishedAt(java.time.LocalDateTime.now());

        return knowledgeEntryRepository.save(entry);
    }

    /**
     * 审核驳回
     */
    public KnowledgeEntry rejectEntry(Integer entryId, Integer reviewerId) {
        KnowledgeEntry entry = getEntryById(entryId);

        // 修改状态为 rejected
        entry.setStatus(KnowledgeEntry.Status.rejected);
        // 记录是谁驳回的
        entry.setReviewerId(reviewerId);

        return knowledgeEntryRepository.save(entry);
    }

}