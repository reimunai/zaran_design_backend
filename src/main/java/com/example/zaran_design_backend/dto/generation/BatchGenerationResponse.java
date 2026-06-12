package com.example.zaran_design_backend.dto.generation;

import java.util.List;

/** 批量生成任务响应 */
public class BatchGenerationResponse {

    private String batchId;
    private List<TaskBrief> tasks;
    private String websocketUrl;

    public BatchGenerationResponse() {}

    public BatchGenerationResponse(String batchId, List<TaskBrief> tasks, String websocketUrl) {
        this.batchId = batchId;
        this.tasks = tasks;
        this.websocketUrl = websocketUrl;
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public List<TaskBrief> getTasks() { return tasks; }
    public void setTasks(List<TaskBrief> tasks) { this.tasks = tasks; }

    public String getWebsocketUrl() { return websocketUrl; }
    public void setWebsocketUrl(String websocketUrl) { this.websocketUrl = websocketUrl; }

    /** 任务简要信息 */
    public static class TaskBrief {
        private String taskId;
        private String status;

        public TaskBrief() {}

        public TaskBrief(String taskId, String status) {
            this.taskId = taskId;
            this.status = status;
        }

        public String getTaskId() { return taskId; }
        public void setTaskId(String taskId) { this.taskId = taskId; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}