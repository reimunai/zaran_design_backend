package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>,
        JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);

    /** 按角色统计用户数 */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(@Param("role") User.Role role);

    /** 统计指定时间之后创建的用户数 */
    long countByCreatedAtAfter(LocalDateTime dateTime);

    /** 统计指定时间之后更新过的用户数（近似活跃用户） */
    long countByUpdatedAtAfter(LocalDateTime dateTime);
}
