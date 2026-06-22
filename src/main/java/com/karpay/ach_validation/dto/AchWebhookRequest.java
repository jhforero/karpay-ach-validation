package com.karpay.ach_validation.dto;

import jakarta.validation.constraints.NotBlank;

public record AchWebhookRequest(
        @NotBlank String reference,
        @NotBlank String status
) {}