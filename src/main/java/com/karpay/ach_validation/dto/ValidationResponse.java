package com.karpay.ach_validation.dto;

import com.karpay.ach_validation.domain.Validation;

public record ValidationResponse(String validationId, String status, String achReference) {

    public static ValidationResponse from(Validation v) {
        return new ValidationResponse(
                v.getId().toString(),
                v.getStatus().name(),
                v.getAchReference());
    }
}