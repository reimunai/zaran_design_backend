package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.PatternLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatternLikeRepository extends JpaRepository<PatternLike, Integer> {

    Optional<PatternLike> findByPatternIdAndUserId(Integer patternId, Integer userId);

    boolean existsByPatternIdAndUserId(Integer patternId, Integer userId);

    long countByPatternId(Integer patternId);
}
