package com.example.zaran_design_backend.service;

import com.example.zaran_design_backend.entity.TermDictionary;
import com.example.zaran_design_backend.repository.TermDictionaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TermDictionaryService {

    private final TermDictionaryRepository termDictionaryRepository;

    public TermDictionaryService(TermDictionaryRepository termDictionaryRepository) {
        this.termDictionaryRepository = termDictionaryRepository;
    }

    // 获取所有术语列表
    public List<TermDictionary> getAllTerms() {
        return termDictionaryRepository.findAll();
    }
}