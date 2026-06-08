package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.KnowledgeNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KnowledgeNoteRepository extends JpaRepository<KnowledgeNote, Integer> {

    // 必须在这里声明，Service 层才能调用！

    // 找出某个用户的所有收藏和笔记
    List<KnowledgeNote> findByUserId(Integer userId);

    // 判断某个用户是否收藏过某个特定的知识点
    Optional<KnowledgeNote> findByUserIdAndEntryId(Integer userId, Integer entryId);
}