package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.CollabSessionVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollabSessionVersionRepository extends JpaRepository<CollabSessionVersion, Integer> {

    /** 查询某会话当前最大版本号 */
    Optional<CollabSessionVersion> findTopBySessionIdOrderByVersionNumberDesc(Integer sessionId);

    /** 按会话ID和版本ID查询 */
    Optional<CollabSessionVersion> findByVersionIdAndSessionId(Integer versionId, Integer sessionId);
}
