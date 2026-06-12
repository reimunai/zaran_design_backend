package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.CollabParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollabParticipantRepository extends JpaRepository<CollabParticipant, Integer> {

    /** 查询某会话的所有参与者 */
    List<CollabParticipant> findBySessionId(Integer sessionId);

    /** 查询某用户在某会话中的参与记录 */
    Optional<CollabParticipant> findBySessionIdAndUserId(Integer sessionId, Integer userId);

    /** 判断用户是否为某会话的参与者 */
    boolean existsBySessionIdAndUserId(Integer sessionId, Integer userId);

    /** 删除某会话中的所有参与者 */
    void deleteBySessionId(Integer sessionId);
}
