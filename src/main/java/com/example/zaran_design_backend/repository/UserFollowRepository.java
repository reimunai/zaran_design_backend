package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserFollowRepository extends JpaRepository<UserFollow, Integer> {
    Optional<UserFollow> findByFollowerIdAndFollowingId(Integer followerId, Integer followingId);
    long countByFollowerId(Integer followerId);
    long countByFollowingId(Integer followingId);
    boolean existsByFollowerIdAndFollowingId(Integer followerId, Integer followingId);
    void deleteByFollowerIdAndFollowingId(Integer followerId, Integer followingId);
}
