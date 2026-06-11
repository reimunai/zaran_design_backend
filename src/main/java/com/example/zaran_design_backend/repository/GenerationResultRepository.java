package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.GenerationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenerationResultRepository extends JpaRepository<GenerationResult, Long> {

    /** 根据任务ID查询结果 */
    Optional<GenerationResult> findByTaskId(String taskId);

    /** 根据结果ID和任务ID查询（验证所有权） */
    Optional<GenerationResult> findByResultIdAndTaskId(Long resultId, String taskId);
}