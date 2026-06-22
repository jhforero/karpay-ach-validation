package com.karpay.ach_validation.service;

import com.karpay.ach_validation.domain.Validation;
import com.karpay.ach_validation.domain.ValidationStatus;
import com.karpay.ach_validation.domain.WebhookEvent;
import com.karpay.ach_validation.dto.AchWebhookRequest;
import com.karpay.ach_validation.exception.NotFoundException;
import com.karpay.ach_validation.repository.ValidationRepository;
import com.karpay.ach_validation.repository.WebhookEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WebhookService {

    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);

    private final ValidationRepository validationRepo;
    private final WebhookEventRepository eventRepo;

    public WebhookService(ValidationRepository validationRepo,
                          WebhookEventRepository eventRepo) {
        this.validationRepo = validationRepo;
        this.eventRepo = eventRepo;
    }

    @Transactional
    public void processWebhook(AchWebhookRequest req) {

        String eventId = req.reference() + ":" + req.status();
        if (eventRepo.existsByEventId(eventId)) {
            log.info("Evento duplicado ignorado: {}", eventId);
            return;
        }

        WebhookEvent event = WebhookEvent.builder()
                .eventId(eventId)
                .achReference(req.reference())
                .payload(req.toString())
                .build();
        eventRepo.save(event);

        Validation validation = validationRepo.findByAchReference(req.reference())
                .orElseThrow(() -> new NotFoundException(
                        "No existe validación con referencia ACH: " + req.reference()));

        validation.setStatus(mapStatus(req.status()));
        validationRepo.save(validation);

        log.info("Validación {} actualizada a {}", validation.getId(), validation.getStatus());
    }


    private ValidationStatus mapStatus(String achStatus) {
        return switch (achStatus.toUpperCase()) {
            case "APPROVED" -> ValidationStatus.APPROVED;
            case "REJECTED" -> ValidationStatus.REJECTED;
            default -> ValidationStatus.FAILED;
        };
    }
}