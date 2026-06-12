package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.CollabOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollabOperationRepository extends JpaRepository<CollabOperation, Integer> {

    /** 按会话分页查询操作记录，按版本号倒序 */
    Page<CollabOperation> findBySessionIdOrderByVersionNumberDesc(Integer sessionId, Pageable pageable);

    /** 按会话和版本号范围查询操作记录 */
    Page<CollabOperation> findBySessionIdAndVersionNumberBetweenOrderByVersionNumberAsc(
            Integer sessionId, Integer versionStart, Integer versionEnd, Pageable pageable);

    /** 查询某会话当前最大版本号 */
    Optional<CollabOperation> findTopBySessionIdOrderByVersionNumberDesc(Integer sessionId);
}
