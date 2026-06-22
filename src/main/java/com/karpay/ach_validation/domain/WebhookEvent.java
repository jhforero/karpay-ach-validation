package com.karpay.ach_validation.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhook_events",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_webhook_event",
                columnNames = "event_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WebhookEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "event_id", nullable = false, length = 80)
    private String eventId;

    @Column(name = "ach_reference", nullable = false, length = 60)
    private String achReference;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "received_at", nullable = false)
    private OffsetDateTime receivedAt;

    @PrePersist
    void prePersist() {
        this.receivedAt = OffsetDateTime.now();
    }
}