package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OperationLogRepository extends JpaRepository<OperationLog, Integer>,
        JpaSpecificationExecutor<OperationLog> {

    Page<OperationLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
