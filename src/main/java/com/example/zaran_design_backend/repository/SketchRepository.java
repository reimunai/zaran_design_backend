package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.Sketch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SketchRepository extends JpaRepository<Sketch, Integer>,
        JpaSpecificationExecutor<Sketch> {

    /** 按ID查询未删除的草图 */
    Optional<Sketch> findBySketchIdAndDeletedAtIsNull(Integer sketchId);
}
