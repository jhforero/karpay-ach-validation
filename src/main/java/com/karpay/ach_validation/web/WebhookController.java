package com.karpay.ach_validation.web;

import com.karpay.ach_validation.dto.AchWebhookRequest;
import com.karpay.ach_validation.service.WebhookService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhooks")
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping("/ach")
    public ResponseEntity<Void> handleAchWebhook(@Valid @RequestBody AchWebhookRequest request) {
        webhookService.processWebhook(request);
        return ResponseEntity.ok().build();
    }
}