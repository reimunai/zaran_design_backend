package com.example.zaran_design_backend.controller;

import com.example.zaran_design_backend.common.Result;
import com.example.zaran_design_backend.dto.KnowledgeEntryRequest;
import com.example.zaran_design_backend.entity.KnowledgeEntry;
import com.example.zaran_design_backend.entity.TermDictionary;
import com.example.zaran_design_backend.service.KnowledgeEntryService;
import com.example.zaran_design_backend.service.TermDictionaryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeEntryService knowledgeEntryService;
    private TermDictionaryService termDictionaryService = null; // 新增

    public KnowledgeController(KnowledgeEntryService knowledgeEntryService) {
        this.knowledgeEntryService = knowledgeEntryService;
        this.termDictionaryService = termDictionaryService;
    }

    @GetMapping("/entries")
    public Result<List<KnowledgeEntry>> getKnowledgeEntries() {
        List<KnowledgeEntry> entries = knowledgeEntryService.getPublishedEntries();
        return Result.ok("success", entries);
    }
    @GetMapping("/terminology")
    public Result<List<TermDictionary>> getTerminology() {
        List<TermDictionary> terms = termDictionaryService.getAllTerms();
        return Result.ok("success", terms);
    }
    @PostMapping("/entries")
    public Result<KnowledgeEntry> createKnowledgeEntry(@RequestBody KnowledgeEntryRequest request) {
        // 调用厨师保存数据
        KnowledgeEntry savedEntry = knowledgeEntryService.createEntry(request);
        // 返回符合接口文档要求的 JSON 响应
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

}