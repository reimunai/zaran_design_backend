package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.KnowledgePatternLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KnowledgePatternLinkRepository extends JpaRepository<KnowledgePatternLink, Integer> {

    // 方便以后写接口：根据知识ID，找出所有关联的图案链接
    List<KnowledgePatternLink> findByEntryId(Integer entryId);
}