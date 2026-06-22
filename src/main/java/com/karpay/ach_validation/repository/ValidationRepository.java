package com.karpay.ach_validation.repository;

import com.karpay.ach_validation.domain.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface ValidationRepository extends JpaRepository<Validation, UUID> {
    Optional<Validation> findByRequestId(String requestId);
    Optional<Validation> findByAchReference(String achReference);
}