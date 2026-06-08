package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.KnowledgeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeEntryRepository extends JpaRepository<KnowledgeEntry, Integer> {

    List<KnowledgeEntry> findByStatusOrderByCreatedAtDesc(KnowledgeEntry.Status status);

}