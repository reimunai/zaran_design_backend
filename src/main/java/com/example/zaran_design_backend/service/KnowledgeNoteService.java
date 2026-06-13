package com.example.zaran_design_backend.service;

import com.example.zaran_design_backend.dto.KnowledgeNoteRequest;
import com.example.zaran_design_backend.entity.KnowledgeNote;
import com.example.zaran_design_backend.repository.KnowledgeNoteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KnowledgeNoteService {

    private final KnowledgeNoteRepository knowledgeNoteRepository;

    public KnowledgeNoteService(KnowledgeNoteRepository knowledgeNoteRepository) {
        this.knowledgeNoteRepository = knowledgeNoteRepository;
    }

    /**
     * 获取某个用户的所有收藏和笔记
     */
    public List<KnowledgeNote> getUserNotes(Integer userId) {
        return knowledgeNoteRepository.findByUserId(userId);
    }

    /**
     * 智能判断：Save Or Update
     * - 若用户首次收藏该知识点 → 新建记录
     * - 若用户已收藏过 → 更新其个人笔记内容
     */
    public KnowledgeNote saveOrUpdateNote(KnowledgeNoteRequest request) {
        Optional<KnowledgeNote> existingNote = knowledgeNoteRepository
                .findByUserIdAndEntryId(request.getUserId(), request.getEntryId());

        if (existingNote.isPresent()) {
            // 已收藏 → 更新笔记
            KnowledgeNote note = existingNote.get();
            note.setPersonalNote(request.getPersonalNote());
            return knowledgeNoteRepository.save(note);
        } else {
            // 首次收藏 → 新建记录
            KnowledgeNote note = new KnowledgeNote();
            note.setUserId(request.getUserId());
            note.setEntryId(request.getEntryId());
            note.setPersonalNote(request.getPersonalNote());
            return knowledgeNoteRepository.save(note);
        }
    }

    /**
     * 查询某个用户对某个知识点的笔记
     */
    public KnowledgeNote getUserNoteForEntry(Integer userId, Integer entryId) {
        return knowledgeNoteRepository.findByUserIdAndEntryId(userId, entryId).orElse(null);
    }

    /**
     * 取消收藏（删除笔记记录）
     */
    public void removeNote(Integer userId, Integer entryId) {
        Optional<KnowledgeNote> existingNote = knowledgeNoteRepository
                .findByUserIdAndEntryId(userId, entryId);
        existingNote.ifPresent(knowledgeNoteRepository::delete);
    }
}
