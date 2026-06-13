package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.GenerationParamHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenerationParamHistoryRepository extends JpaRepository<GenerationParamHistory, Long> {

    /** 查询用户的常用参数（分页） */
    Page<GenerationParamHistory> findByUserIdOrderByUseCountDescLastUsedAtDesc(Integer userId, Pageable pageable);

    /** 根据用户ID和参数组合查找记录 */
    Optional<GenerationParamHistory> findByUserIdAndKValueAndNoiseLevelAndPatchMode(
            Integer userId, Integer kValue, Float noiseLevel, Integer patchMode);

    /** 更新使用次数和最后使用时间 */
    @Modifying
    @Query("UPDATE GenerationParamHistory h SET h.useCount = h.useCount + 1, h.lastUsedAt = :now WHERE h.historyId = :id")
    int incrementUseCount(@Param("id") Long historyId, @Param("now") java.time.LocalDateTime now);
}