package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.PatternReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatternReportRepository extends JpaRepository<PatternReport, Integer> {

    boolean existsByPatternIdAndUserId(Integer patternId, Integer userId);
}
