package com.example.zaran_design_backend.dto.generation;

/** 生成任务提交响应 */
public class GenerationTaskResponse {

    private String taskId;
    private String status;
    private Integer queuePosition;
    private Integer estimatedWaitSeconds;
    private String websocketUrl;

    public GenerationTaskResponse() {}

    public GenerationTaskResponse(String taskId, String status, Integer queuePosition, 
                                  Integer estimatedWaitSeconds, String websocketUrl) {
        this.taskId = taskId;
        this.status = status;
        this.queuePosition = queuePosition;
        this.estimatedWaitSeconds = estimatedWaitSeconds;
        this.websocketUrl = websocketUrl;
    }

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getQueuePosition() { return queuePosition; }
    public void setQueuePosition(Integer queuePosition) { this.queuePosition = queuePosition; }

    public Integer getEstimatedWaitSeconds() { return estimatedWaitSeconds; }
    public void setEstimatedWaitSeconds(Integer estimatedWaitSeconds) { this.estimatedWaitSeconds = estimatedWaitSeconds; }

    public String getWebsocketUrl() { return websocketUrl; }
    public void setWebsocketUrl(String websocketUrl) { this.websocketUrl = websocketUrl; }
}