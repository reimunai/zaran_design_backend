package com.example.zaran_design_backend.controller;

import com.example.zaran_design_backend.common.Result;
import com.example.zaran_design_backend.dto.PageResponse;
import com.example.zaran_design_backend.dto.generation.*;
import com.example.zaran_design_backend.entity.GenerationTask;
import com.example.zaran_design_backend.service.GenerationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI生成模块（第5模块）。
 * 业务异常由 GlobalExceptionHandler 统一转换为带文档错误码的响应。
 */
@RestController
@RequestMapping("/api/generation")
public class GenerationController {

    private final GenerationService generationService;

    public GenerationController(GenerationService generationService) {
        this.generationService = generationService;
    }

    /**
     * 5.2.1 提交生成任务
     * POST /api/generation/tasks
     */
    @PostMapping("/tasks")
    public Result<GenerationTaskResponse> submitTask(@Valid @RequestBody CreateGenerationTaskRequest request) {
        Integer userId = currentUserId();
        GenerationTaskResponse response = generationService.submitTask(userId, request);
        return Result.ok("任务已提交", response);
    }

    /**
     * 查询我的生成任务列表
     * GET /api/generation/tasks
     */
    @GetMapping("/tasks")
    public Result<PageResponse<GenerationTaskDetailResponse>> listTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Integer userId = currentUserId();
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 1);
        Page<GenerationTask> taskPage = generationService.listMyTasks(userId, safePage, safeSize);
        
        List<GenerationTaskDetailResponse> list = taskPage.getContent().stream()
                .map(GenerationTaskDetailResponse::of)
                .collect(Collectors.toList());
        
        PageResponse<GenerationTaskDetailResponse> response = new PageResponse<>(
                list, taskPage.getTotalElements(), safePage, safeSize, taskPage.getTotalPages());
        
        return Result.ok(response);
    }

    /**
     * 5.2.3 获取任务详情与进度
     * GET /api/generation/tasks/{taskId}
     */
    @GetMapping("/tasks/{taskId}")
    public Result<GenerationTaskDetailResponse> getTaskDetail(@PathVariable String taskId) {
        Integer userId = currentUserId();
        return Result.ok(generationService.getTaskDetail(userId, taskId));
    }

    /**
     * 取消排队中的任务
     * DELETE /api/generation/tasks/{taskId}
     */
    @DeleteMapping("/tasks/{taskId}")
    public Result<Void> cancelTask(@PathVariable String taskId) {
        Integer userId = currentUserId();
        generationService.cancelTask(userId, taskId);
        return Result.ok("任务已取消", null);
    }

    /**
     * 5.2.2 批量提交生成任务
     * POST /api/generation/tasks/batch
     */
    @PostMapping("/tasks/batch")
    public Result<BatchGenerationResponse> submitBatchTasks(@Valid @RequestBody BatchGenerationRequest request) {
        Integer userId = currentUserId();
        BatchGenerationResponse response = generationService.submitBatchTasks(userId, request);
        return Result.ok("批量任务已提交", response);
    }

    /**
     * 5.2.4 获取生成结果
     * GET /api/generation/tasks/{taskId}/result
     */
    @GetMapping("/tasks/{taskId}/result")
    public Result<GenerationResultResponse> getResult(@PathVariable String taskId) {
        Integer userId = currentUserId();
        return Result.ok(generationService.getResult(userId, taskId));
    }

    /**
     * 5.2.5 对生成结果评分
     * POST /api/generation/results/{resultId}/rate
     */
    @PostMapping("/results/{resultId}/rate")
    public Result<Void> rateResult(@PathVariable Long resultId, @Valid @RequestBody RateRequest request) {
        generationService.rateResult(resultId, request);
        return Result.ok("评分成功", null);
    }

    /**
     * 5.2.6 下载生成结果
     * POST /api/generation/results/{resultId}/download
     */
    @PostMapping("/results/{resultId}/download")
    public Result<DownloadResponse> downloadResult(@PathVariable Long resultId, 
                                                   @RequestBody(required = false) DownloadRequest request) {
        if (request == null) {
            request = new DownloadRequest();
        }
        return Result.ok(generationService.downloadResult(resultId, request));
    }

    /**
     * 5.2.7 获取我的常用参数
     * GET /api/generation/params/history
     */
    @GetMapping("/params/history")
    public Result<PageResponse<ParamHistoryResponse>> getParamHistory(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Integer userId = currentUserId();
        int safeSize = Math.min(Math.max(size, 1), 50);
        int safePage = Math.max(page, 1);
        Page<ParamHistoryResponse> historyPage = generationService.getParamHistory(userId, safePage, safeSize);
        
        PageResponse<ParamHistoryResponse> response = new PageResponse<>(
                historyPage.getContent(), 
                historyPage.getTotalElements(), 
                safePage, 
                safeSize, 
                historyPage.getTotalPages());
        
        return Result.ok(response);
    }

    /**
     * 5.2.8 获取当前队列状态
     * GET /api/generation/queue/status
     */
    @GetMapping("/queue/status")
    public Result<QueueStatusResponse> getQueueStatus() {
        return Result.ok(generationService.getQueueStatus());
    }

    // ============================ 安全上下文辅助 ============================

    private Integer currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Integer) {
            return (Integer) auth.getPrincipal();
        }
        throw new com.example.zaran_design_backend.service.BusinessException(401, "未登录");
    }
}