package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.PatternReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatternReportRepository extends JpaRepository<PatternReport, Integer> {

    boolean existsByPatternIdAndUserId(Integer patternId, Integer userId);

    /** 按状态分页查询举报记录（按时间倒序） */
    Page<PatternReport> findByStatusOrderByCreatedAtDesc(String status, Pageable pageable);

    /** 分页查询全部举报记录 */
    Page<PatternReport> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /** 按作品ID查询举报记录 */
    List<PatternReport> findByPatternId(Integer patternId);
}
