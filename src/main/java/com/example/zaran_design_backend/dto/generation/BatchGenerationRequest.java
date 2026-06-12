package com.example.zaran_design_backend.dto.generation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/** 批量生成任务请求 */
public class BatchGenerationRequest {

    @NotEmpty(message = "tasks不能为空")
    @Size(max = 5, message = "tasks最多包含5个任务")
    @Valid
    private List<CreateGenerationTaskRequest> tasks;

    public List<CreateGenerationTaskRequest> getTasks() { return tasks; }
    public void setTasks(List<CreateGenerationTaskRequest> tasks) { this.tasks = tasks; }
}