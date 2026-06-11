package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.GenerationTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenerationTaskRepository extends JpaRepository<GenerationTask, String> {

    /** 查询用户的任务列表（分页） */
    Page<GenerationTask> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    /** 查询用户指定状态的任务数量 */
    long countByUserIdAndStatus(Integer userId, String status);

    /** 查询队列中的任务（按优先级和创建时间排序） */
    List<GenerationTask> findByStatusOrderByPriorityDescCreatedAtAsc(String status);

    /** 获取用户任务队列位置 */
    @Query("SELECT COUNT(t) FROM GenerationTask t WHERE t.status = 'queued' AND t.priority >= :priority AND t.createdAt <= :createdAt")
    long countQueuePosition(@Param("priority") Integer priority, @Param("createdAt") java.time.LocalDateTime createdAt);

    /** 根据任务ID和用户ID查询 */
    Optional<GenerationTask> findByTaskIdAndUserId(String taskId, Integer userId);

    /** 查询队列长度 */
    long countByStatus(String status);

    /** 查询处理中的任务数 */
    long countByStatusIn(List<String> statuses);
}