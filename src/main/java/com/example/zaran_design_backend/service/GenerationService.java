package com.example.zaran_design_backend.service;

import com.example.zaran_design_backend.dto.generation.*;
import com.example.zaran_design_backend.entity.GenerationTask;
import com.example.zaran_design_backend.entity.GenerationResult;
import com.example.zaran_design_backend.repository.GenerationParamHistoryRepository;
import com.example.zaran_design_backend.repository.GenerationResultRepository;
import com.example.zaran_design_backend.repository.GenerationTaskRepository;
import com.example.zaran_design_backend.repository.SketchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AI生成模块业务逻辑。
 */
@Service
public class GenerationService {

    private final GenerationTaskRepository taskRepository;
    private final GenerationResultRepository resultRepository;
    private final GenerationParamHistoryRepository paramHistoryRepository;
    private final SketchRepository sketchRepository;

    // 模拟配置
    private static final int MAX_CONCURRENT_TASKS = 20;
    private static final int AVG_TASK_DURATION_SECONDS = 15;
    private static final String BASE_WEBSOCKET_URL = "wss://api.zaran.com/ws/generation";

    public GenerationService(GenerationTaskRepository taskRepository,
                             GenerationResultRepository resultRepository,
                             GenerationParamHistoryRepository paramHistoryRepository,
                             SketchRepository sketchRepository) {
        this.taskRepository = taskRepository;
        this.resultRepository = resultRepository;
        this.paramHistoryRepository = paramHistoryRepository;
        this.sketchRepository = sketchRepository;
    }

    // ============================ 5.2.1 提交生成任务 ============================

    @Transactional
    public GenerationTaskResponse submitTask(Integer userId, CreateGenerationTaskRequest request) {
        // 校验草图是否存在且属于用户
        validateSketchAccess(userId, request.getSketchId());

        // 参数校验（已通过DTO注解校验，此处做业务校验）
        validateGenerationParams(request);

        // 生成任务ID
        String taskId = generateTaskId();

        // 创建任务
        GenerationTask task = new GenerationTask();
        task.setTaskId(taskId);
        task.setUserId(userId);
        task.setSketchId(request.getSketchId());
        task.setkValue(request.getkValue());
        task.setNoiseLevel(request.getNoiseLevel());
        task.setPatchMode(request.getPatchMode());
        task.setStyleRefId(request.getStyleRefId());
        task.setPriority(request.getPriority() != null ? request.getPriority() : 5);
        task.setStatus("queued");
        task.setProgress(0);
        task.setStage("queued");

        // 计算队列位置
        int queuePosition = calculateQueuePosition(task.getPriority(), task.getCreatedAt());
        task.setQueuePosition(queuePosition);

        // 估算等待时间（平均每个任务15秒）
        int estimatedWaitSeconds = queuePosition * AVG_TASK_DURATION_SECONDS;
        task.setEstimatedCompleteAt(LocalDateTime.now().plusSeconds(estimatedWaitSeconds + AVG_TASK_DURATION_SECONDS));

        taskRepository.save(task);

        // 更新参数历史
        updateParamHistory(userId, request.getkValue(), request.getNoiseLevel(), request.getPatchMode());

        return new GenerationTaskResponse(
                taskId,
                "queued",
                queuePosition,
                estimatedWaitSeconds,
                BASE_WEBSOCKET_URL + "/" + taskId
        );
    }

    // ============================ 5.2.2 批量提交生成任务 ============================

    @Transactional
    public BatchGenerationResponse submitBatchTasks(Integer userId, BatchGenerationRequest request) {
        String batchId = "batch_" + System.currentTimeMillis() / 1000 + "_" + 
                UUID.randomUUID().toString().substring(0, 4);

        List<BatchGenerationResponse.TaskBrief> taskBriefs = new ArrayList<>();

        for (CreateGenerationTaskRequest taskRequest : request.getTasks()) {
            // 校验草图
            validateSketchAccess(userId, taskRequest.getSketchId());
            validateGenerationParams(taskRequest);

            String taskId = generateTaskId();

            GenerationTask task = new GenerationTask();
            task.setTaskId(taskId);
            task.setUserId(userId);
            task.setSketchId(taskRequest.getSketchId());
            task.setkValue(taskRequest.getkValue());
            task.setNoiseLevel(taskRequest.getNoiseLevel());
            task.setPatchMode(taskRequest.getPatchMode());
            task.setStyleRefId(taskRequest.getStyleRefId());
            task.setPriority(taskRequest.getPriority() != null ? taskRequest.getPriority() : 5);
            task.setStatus("queued");
            task.setProgress(0);
            task.setStage("queued");

            taskRepository.save(task);

            // 更新参数历史
            updateParamHistory(userId, taskRequest.getkValue(), taskRequest.getNoiseLevel(), taskRequest.getPatchMode());

            taskBriefs.add(new BatchGenerationResponse.TaskBrief(taskId, "queued"));
        }

        return new BatchGenerationResponse(
                batchId,
                taskBriefs,
                BASE_WEBSOCKET_URL + "/batch/" + batchId
        );
    }

    // ============================ 5.2.3 获取任务详情与进度 ============================

    public GenerationTaskDetailResponse getTaskDetail(Integer userId, String taskId) {
        GenerationTask task = taskRepository.findByTaskIdAndUserId(taskId, userId)
                .orElseThrow(() -> new BusinessException(404, "任务不存在或无权访问"));
        return GenerationTaskDetailResponse.of(task);
    }

    // ============================ 获取我的生成任务列表 ============================

    public Page<GenerationTask> listMyTasks(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return taskRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    // ============================ 5.2.4 获取生成结果 ============================

    public GenerationResultResponse getResult(Integer userId, String taskId) {
        // 验证任务属于用户
        GenerationTask task = taskRepository.findByTaskIdAndUserId(taskId, userId)
                .orElseThrow(() -> new BusinessException(404, "任务不存在或无权访问"));

        // 检查任务状态
        if (!"completed".equals(task.getStatus())) {
            throw new BusinessException(400, "任务尚未完成，无法获取结果");
        }

        GenerationResult result = resultRepository.findByTaskId(taskId)
                .orElseThrow(() -> new BusinessException(404, "生成结果不存在"));

        return GenerationResultResponse.of(result);
    }

    // ============================ 5.2.5 对生成结果评分 ============================

    @Transactional
    public void rateResult(Long resultId, RateRequest request) {
        GenerationResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new BusinessException(404, "结果不存在"));

        result.setRating(request.getRating());
        result.setComment(request.getComment());
        if (request.getIsFavorite() != null) {
            result.setIsFavorite(request.getIsFavorite());
        }

        resultRepository.save(result);
    }

    // ============================ 5.2.6 下载生成结果 ============================

    public DownloadResponse downloadResult(Long resultId, DownloadRequest request) {
        GenerationResult result = resultRepository.findById(resultId)
                .orElseThrow(() -> new BusinessException(404, "结果不存在"));

        String format = request.getFormat() != null ? request.getFormat().toLowerCase() : "png";
        if (!format.equals("png") && !format.equals("jpg")) {
            format = "png";
        }

        Integer resolution = request.getResolution();
        if (resolution == null || (resolution != 1024 && resolution != 2048 && resolution != 4096)) {
            resolution = 4096; // 默认原图
        }

        // 生成带token的下载URL（模拟）
        String token = UUID.randomUUID().toString().substring(0, 32);
        String downloadUrl = result.getImageUrl().replace(".png", "_" + resolution + ".png") + "?token=" + token;

        return new DownloadResponse(downloadUrl, 300, format, resolution);
    }

    // ============================ 5.2.7 获取我的常用参数 ============================

    public Page<ParamHistoryResponse> getParamHistory(Integer userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<com.example.zaran_design_backend.entity.GenerationParamHistory> historyPage = 
                paramHistoryRepository.findByUserIdOrderByUseCountDescLastUsedAtDesc(userId, pageable);
        return historyPage.map(ParamHistoryResponse::of);
    }

    // ============================ 5.2.8 获取当前队列状态 ============================

    public QueueStatusResponse getQueueStatus() {
        long queueLength = taskRepository.countByStatus("queued");
        long processingCount = taskRepository.countByStatusIn(Arrays.asList("processing"));
        int avgWaitTime = (int) (queueLength * AVG_TASK_DURATION_SECONDS / Math.max(MAX_CONCURRENT_TASKS, processingCount));

        String status;
        if (queueLength > 100) {
            status = "busy";
        } else if (queueLength > 50) {
            status = "normal";
        } else {
            status = "idle";
        }

        return new QueueStatusResponse(
                (int) queueLength,
                (int) processingCount,
                MAX_CONCURRENT_TASKS,
                avgWaitTime,
                status
        );
    }

    // ============================ 5.2.4 取消排队中的任务 ============================

    @Transactional
    public void cancelTask(Integer userId, String taskId) {
        GenerationTask task = taskRepository.findByTaskIdAndUserId(taskId, userId)
                .orElseThrow(() -> new BusinessException(404, "任务不存在或无权访问"));

        if (!"queued".equals(task.getStatus())) {
            throw new BusinessException(400, "只能取消排队中的任务");
        }

        task.setStatus("canceled");
        task.setCompletedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    // ============================ 辅助方法 ============================

    private String generateTaskId() {
        return "gen_" + System.currentTimeMillis() / 1000 + "_" + 
                UUID.randomUUID().toString().substring(0, 4);
    }

    private void validateSketchAccess(Integer userId, Long sketchId) {
        // 草图ID可能是Integer或Long，这里统一处理
        Integer intSketchId = sketchId.intValue();
        if (!sketchRepository.existsBySketchIdAndUserId(intSketchId, userId)) {
            throw new BusinessException(404, "草图不存在或无权访问");
        }
    }

    private void validateGenerationParams(CreateGenerationTaskRequest request) {
        if (request.getkValue() != null && (request.getkValue() < 2 || request.getkValue() > 4)) {
            throw new BusinessException(400, "kValue必须为2、3或4");
        }
        if (request.getNoiseLevel() != null && (request.getNoiseLevel() < 0.0 || request.getNoiseLevel() > 1.0)) {
            throw new BusinessException(400, "noiseLevel必须在0.0-1.0之间");
        }
        if (request.getPatchMode() != null && !Arrays.asList(1, 2, 4, 8).contains(request.getPatchMode())) {
            throw new BusinessException(400, "patchMode必须为1、2、4或8");
        }
    }

    private int calculateQueuePosition(Integer priority, LocalDateTime createdAt) {
        // 简化计算：统计优先级>=当前优先级且创建时间<=当前时间的排队任务数
        return (int) taskRepository.countQueuePosition(priority, createdAt);
    }

    @Transactional
    private void updateParamHistory(Integer userId, Integer kValue, Float noiseLevel, Integer patchMode) {
        // 查找是否已有相同参数组合
        paramHistoryRepository.findByUserIdAndKValueAndNoiseLevelAndPatchMode(userId, kValue, noiseLevel, patchMode)
                .ifPresentOrElse(
                        existing -> paramHistoryRepository.incrementUseCount(existing.getHistoryId(), LocalDateTime.now()),
                        () -> {
                            com.example.zaran_design_backend.entity.GenerationParamHistory newHistory = 
                                    new com.example.zaran_design_backend.entity.GenerationParamHistory();
                            newHistory.setUserId(userId);
                            newHistory.setkValue(kValue);
                            newHistory.setNoiseLevel(noiseLevel);
                            newHistory.setPatchMode(patchMode);
                            newHistory.setUseCount(1);
                            newHistory.setLastUsedAt(LocalDateTime.now());
                            paramHistoryRepository.save(newHistory);
                        }
                );
    }
}