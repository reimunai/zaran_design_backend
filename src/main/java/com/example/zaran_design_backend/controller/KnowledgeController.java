package com.example.zaran_design_backend.controller;

import com.example.zaran_design_backend.common.Result;
import com.example.zaran_design_backend.dto.KnowledgeEntryRequest;
import com.example.zaran_design_backend.dto.KnowledgeNoteRequest;
import com.example.zaran_design_backend.entity.KnowledgeEntry;
import com.example.zaran_design_backend.entity.KnowledgeNote;
import com.example.zaran_design_backend.entity.KnowledgePatternLink;
import com.example.zaran_design_backend.entity.TermDictionary;
import com.example.zaran_design_backend.repository.KnowledgePatternLinkRepository;
import com.example.zaran_design_backend.service.KnowledgeEntryService;
import com.example.zaran_design_backend.service.KnowledgeNoteService;
import com.example.zaran_design_backend.service.TermDictionaryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeEntryService knowledgeEntryService;
    private final TermDictionaryService termDictionaryService;
    private final KnowledgeNoteService knowledgeNoteService;
    private final KnowledgePatternLinkRepository knowledgePatternLinkRepository;

    public KnowledgeController(KnowledgeEntryService knowledgeEntryService,
                               TermDictionaryService termDictionaryService,
                               KnowledgeNoteService knowledgeNoteService,
                               KnowledgePatternLinkRepository knowledgePatternLinkRepository) {
        this.knowledgeEntryService = knowledgeEntryService;
        this.termDictionaryService = termDictionaryService;
        this.knowledgeNoteService = knowledgeNoteService;
        this.knowledgePatternLinkRepository = knowledgePatternLinkRepository;
    }

    // ============================================================
    // 知识条目 CRUD
    // ============================================================

    /**
     * 接口文档 8.2.1：获取已发布知识列表
     * GET /api/knowledge/entries
     */
    @GetMapping("/entries")
    public Result<List<KnowledgeEntry>> getKnowledgeEntries() {
        List<KnowledgeEntry> entries = knowledgeEntryService.getPublishedEntries();
        return Result.ok("success", entries);
    }

    /**
     * 接口文档 8.2.2：提交新知识条目
     * POST /api/knowledge/entries
     */
    @PostMapping("/entries")
    public Result<KnowledgeEntry> createKnowledgeEntry(@RequestBody KnowledgeEntryRequest request) {
        KnowledgeEntry savedEntry = knowledgeEntryService.createEntry(request);
        return Result.ok("知识条目已提交，等待审核", savedEntry);
    }

    /**
     * 接口文档 8.2.3：知识详情
     * GET /api/knowledge/entries/{entryId}
     */
    @GetMapping("/entries/{entryId}")
    public Result<KnowledgeEntry> getEntryDetail(@PathVariable Integer entryId) {
        KnowledgeEntry entry = knowledgeEntryService.getEntryById(entryId);
        return Result.ok("success", entry);
    }

    /**
     * 接口文档 8.2.5：修改知识条目
     * PUT /api/knowledge/entries/{entryId}
     */
    @PutMapping("/entries/{entryId}")
    public Result<KnowledgeEntry> updateKnowledgeEntry(
            @PathVariable Integer entryId,
            @RequestBody KnowledgeEntryRequest request) {
        KnowledgeEntry updatedEntry = knowledgeEntryService.updateEntry(entryId, request);
        return Result.ok("修改成功，已重新提交审核", updatedEntry);
    }

    /**
     * 接口文档 8.2.6：删除知识条目
     * DELETE /api/knowledge/entries/{entryId}
     */
    @DeleteMapping("/entries/{entryId}")
    public Result<Void> deleteKnowledgeEntry(@PathVariable Integer entryId) {
        knowledgeEntryService.deleteEntry(entryId);
        return Result.ok("知识条目删除成功", null);
    }

    // ============================================================
    // 审核工作流
    // ============================================================

    /**
     * 接口文档 8.2.7：获取待审核知识列表
     * GET /api/knowledge/entries/pending
     */
    @GetMapping("/entries/pending")
    public Result<List<KnowledgeEntry>> getPendingEntries() {
        List<KnowledgeEntry> entries = knowledgeEntryService.getPendingEntries();
        return Result.ok("success", entries);
    }

    /**
     * 接口文档 8.2.8：审核通过
     * PUT /api/knowledge/entries/{entryId}/approve
     */
    @PutMapping("/entries/{entryId}/approve")
    public Result<KnowledgeEntry> approveEntry(@PathVariable Integer entryId) {
        // TODO: 目前写死审核人为 2 号用户（传承人张师傅），后期接入 JWT 后动态获取
        Integer reviewerId = 2;
        KnowledgeEntry approvedEntry = knowledgeEntryService.approveEntry(entryId, reviewerId);
        return Result.ok("审核通过，已成功发布", approvedEntry);
    }

    /**
     * 接口文档 8.2.9：审核驳回
     * PUT /api/knowledge/entries/{entryId}/reject
     */
    @PutMapping("/entries/{entryId}/reject")
    public Result<KnowledgeEntry> rejectEntry(@PathVariable Integer entryId) {
        // TODO: 目前写死审核人为 2 号用户（传承人张师傅）
        Integer reviewerId = 2;
        KnowledgeEntry rejectedEntry = knowledgeEntryService.rejectEntry(entryId, reviewerId);
        return Result.ok("已驳回该知识条目", rejectedEntry);
    }

    // ============================================================
    // 术语词典
    // ============================================================

    /**
     * 接口文档 8.3：术语词典查询
     * GET /api/knowledge/terminology
     */
    @GetMapping("/terminology")
    public Result<List<TermDictionary>> getTerminology() {
        List<TermDictionary> terms = termDictionaryService.getAllTerms();
        return Result.ok("success", terms);
    }

    // ============================================================
    // 知识与图案关联
    // ============================================================

    /**
     * 接口文档 8.4：获取知识点关联的图案列表
     * GET /api/knowledge/entries/{entryId}/related-patterns
     */
    @GetMapping("/entries/{entryId}/related-patterns")
    public Result<List<KnowledgePatternLink>> getRelatedPatterns(@PathVariable Integer entryId) {
        List<KnowledgePatternLink> links = knowledgePatternLinkRepository.findByEntryId(entryId);
        return Result.ok("success", links);
    }

    // ============================================================
    // 用户收藏与笔记
    // ============================================================

    /**
     * 接口文档 8.5：收藏知识点 / 提交笔记
     * POST /api/knowledge/entries/{entryId}/notes
     */
    @PostMapping("/entries/{entryId}/notes")
    public Result<KnowledgeNote> saveOrUpdateNote(@PathVariable Integer entryId,
                                                   @RequestBody KnowledgeNoteRequest request) {
        // 确保路径中的 entryId 与请求体中的 entryId 一致
        request.setEntryId(entryId);
        KnowledgeNote note = knowledgeNoteService.saveOrUpdateNote(request);
        return Result.ok("收藏成功", note);
    }

    /**
     * 查询某个用户对某个知识点的笔记（用于回显）
     * GET /api/knowledge/entries/{entryId}/notes?userId={userId}
     */
    @GetMapping("/entries/{entryId}/notes")
    public Result<KnowledgeNote> getUserNoteForEntry(@PathVariable Integer entryId,
                                                      @RequestParam Integer userId) {
        KnowledgeNote note = knowledgeNoteService.getUserNoteForEntry(userId, entryId);
        if (note == null) {
            return Result.ok("该用户尚未收藏此知识点", null);
        }
        return Result.ok("success", note);
    }

    /**
     * 取消收藏
     * DELETE /api/knowledge/entries/{entryId}/notes?userId={userId}
     */
    @DeleteMapping("/entries/{entryId}/notes")
    public Result<Void> removeNote(@PathVariable Integer entryId,
                                    @RequestParam Integer userId) {
        knowledgeNoteService.removeNote(userId, entryId);
        return Result.ok("已取消收藏", null);
    }

}
