package com.karpay.ach_validation.web;

import com.karpay.ach_validation.domain.Validation;
import com.karpay.ach_validation.dto.CreateValidationRequest;
import com.karpay.ach_validation.dto.ValidationResponse;
import com.karpay.ach_validation.service.ValidationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/validations")
public class ValidationController {

    private final ValidationService service;

    public ValidationController(ValidationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ValidationResponse> create(
            @Valid @RequestBody CreateValidationRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        Validation validation = service.createValidation(request, idempotencyKey);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ValidationResponse.from(validation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ValidationResponse> getById(@PathVariable UUID id) {
        Validation validation = service.getValidation(id);
        return ResponseEntity.ok(ValidationResponse.from(validation));
    }
}