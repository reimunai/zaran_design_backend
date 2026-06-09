package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.PatternEdit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatternEditRepository extends JpaRepository<PatternEdit, Integer> {

    /** 按作品ID查询所有编辑记录，按时间升序 */
    List<PatternEdit> findByPatternIdOrderByCreatedAtAsc(Integer patternId);
}
