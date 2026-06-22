package com.karpay.ach_validation.repository;

import com.karpay.ach_validation.domain.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, UUID> {
    boolean existsByEventId(String eventId);
}