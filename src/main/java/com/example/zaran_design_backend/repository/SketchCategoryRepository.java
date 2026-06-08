package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.SketchCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SketchCategoryRepository extends JpaRepository<SketchCategory, Integer> {

    /** 查询全部分类，按排序字段升序，用于构建分类树 */
    List<SketchCategory> findAllByOrderBySortOrderAsc();
}
