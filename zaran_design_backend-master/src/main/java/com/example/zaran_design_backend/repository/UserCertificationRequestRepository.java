package com.example.zaran_design_backend.repository;

import com.example.zaran_design_backend.entity.UserCertificationRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserCertificationRequestRepository extends JpaRepository<UserCertificationRequest, Integer> {
    List<UserCertificationRequest> findByUserId(Integer userId);
    List<UserCertificationRequest> findByStatus(UserCertificationRequest.CertificationStatus status);
}
