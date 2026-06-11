package com.example.zaran_design_backend.dto.generation;

/** 队列状态响应 */
public class QueueStatusResponse {

    private Integer queueLength;
    private Integer processingCount;
    private Integer maxConcurrent;
    private Integer avgWaitTime;
    private String status;

    public QueueStatusResponse() {}

    public QueueStatusResponse(Integer queueLength, Integer processingCount, 
                               Integer maxConcurrent, Integer avgWaitTime, String status) {
        this.queueLength = queueLength;
        this.processingCount = processingCount;
        this.maxConcurrent = maxConcurrent;
        this.avgWaitTime = avgWaitTime;
        this.status = status;
    }

    public Integer getQueueLength() { return queueLength; }
    public void setQueueLength(Integer queueLength) { this.queueLength = queueLength; }

    public Integer getProcessingCount() { return processingCount; }
    public void setProcessingCount(Integer processingCount) { this.processingCount = processingCount; }

    public Integer getMaxConcurrent() { return maxConcurrent; }
    public void setMaxConcurrent(Integer maxConcurrent) { this.maxConcurrent = maxConcurrent; }

    public Integer getAvgWaitTime() { return avgWaitTime; }
    public void setAvgWaitTime(Integer avgWaitTime) { this.avgWaitTime = avgWaitTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}