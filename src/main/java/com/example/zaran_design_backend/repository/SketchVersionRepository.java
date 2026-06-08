package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.SketchVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SketchVersionRepository extends JpaRepository<SketchVersion, Integer> {

    /** 按草图分页查询版本，按版本号倒序 */
    Page<SketchVersion> findBySketchIdOrderByVersionNumberDesc(Integer sketchId, Pageable pageable);

    /** 查询某草图当前最大版本号 */
    Optional<SketchVersion> findTopBySketchIdOrderByVersionNumberDesc(Integer sketchId);

    /** 校验版本是否属于该草图 */
    Optional<SketchVersion> findByVersionIdAndSketchId(Integer versionId, Integer sketchId);
}
