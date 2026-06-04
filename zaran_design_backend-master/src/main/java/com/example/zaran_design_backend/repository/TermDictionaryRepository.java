package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.TermDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermDictionaryRepository extends JpaRepository<TermDictionary, Integer> {
    // JpaRepository 已经自带了 findAll() 方法，暂时不需要我们手写额外的 SQL
}