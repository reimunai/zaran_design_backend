package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.PatternComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatternCommentRepository extends JpaRepository<PatternComment, Integer> {

    /** 按作品ID分页查询顶级评论（parentId 为空），按时间倒序 */
    Page<PatternComment> findByPatternIdAndParentIdIsNullOrderByCreatedAtDesc(
            Integer patternId, Pageable pageable);

    /** 查询某条评论的所有子回复 */
    List<PatternComment> findByParentIdOrderByCreatedAtAsc(Integer parentId);

    /** 按作品ID统计评论数 */
    long countByPatternId(Integer patternId);

    /** 按ID和作品ID查询 */
    Optional<PatternComment> findByCommentIdAndPatternId(Integer commentId, Integer patternId);
}
