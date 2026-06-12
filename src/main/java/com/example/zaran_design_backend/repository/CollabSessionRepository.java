package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.CollabSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CollabSessionRepository extends JpaRepository<CollabSession, Integer> {

    /** 按 sessionToken 查询会话 */
    Optional<CollabSession> findBySessionToken(String sessionToken);

    /** 分页查询某用户参与的活跃会话（作为所有者或参与者），按创建时间倒序 */
    @Query("SELECT DISTINCT s FROM CollabSession s " +
           "LEFT JOIN CollabParticipant p ON s.sessionId = p.sessionId " +
           "WHERE (s.ownerId = :userId OR p.userId = :userId) " +
           "AND s.status <> 'closed' " +
           "ORDER BY s.createdAt DESC")
    Page<CollabSession> findMySessions(@Param("userId") Integer userId, Pageable pageable);
}
