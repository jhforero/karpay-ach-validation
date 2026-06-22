package com.karpay.ach_validation.service;

import com.karpay.ach_validation.client.AchClient;
import com.karpay.ach_validation.domain.*;
import com.karpay.ach_validation.dto.AchValidateRequest;
import com.karpay.ach_validation.dto.AchValidateResponse;
import com.karpay.ach_validation.dto.CreateValidationRequest;
import com.karpay.ach_validation.exception.NotFoundException;
import com.karpay.ach_validation.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class ValidationService {

    private final CustomerRepository customerRepo;
    private final BankAccountRepository accountRepo;
    private final ValidationRepository validationRepo;
    private final AchClient achClient;

    public ValidationService(CustomerRepository customerRepo,
                             BankAccountRepository accountRepo,
                             ValidationRepository validationRepo,
                             AchClient achClient) {
        this.customerRepo = customerRepo;
        this.accountRepo = accountRepo;
        this.validationRepo = validationRepo;
        this.achClient = achClient;
    }

    @Transactional
    public Validation createValidation(CreateValidationRequest req, String idempotencyKey) {
        String requestId = generateRequestId(req, idempotencyKey);

        var existente = validationRepo.findByRequestId(requestId);
        if (existente.isPresent()) {
            return existente.get();
        }

        Customer customer = customerRepo
                .findByDocumentTypeAndDocumentNumber("CC", req.customerDocument())
                .orElseGet(() -> customerRepo.save(
                        Customer.builder()
                                .documentType("CC")
                                .documentNumber(req.customerDocument())
                                .name("Cliente " + req.customerDocument())
                                .build()));


        BankAccount account = accountRepo
                .findByCustomerIdAndBankCodeAndAccountNumber(
                        customer.getId(), req.bankCode(), req.accountNumber())
                .orElseGet(() -> accountRepo.save(
                        BankAccount.builder()
                                .customer(customer)
                                .bankCode(req.bankCode())
                                .accountType(req.accountType())
                                .accountNumber(req.accountNumber())
                                .status("PENDING")
                                .build()));

        Validation validation = Validation.builder()
                .account(account)
                .requestId(requestId)
                .status(ValidationStatus.PENDING)
                .build();

        AchValidateRequest achRequest = new AchValidateRequest(
                req.bankCode(), req.accountType(), req.accountNumber());
        AchValidateResponse achResponse = achClient.validate(achRequest);

        validation.setAchReference(achResponse.reference());
        validation.setStatus(ValidationStatus.PROCESSING);

        return validationRepo.save(validation);
    }

    @Transactional(readOnly = true)
    public Validation getValidation(UUID id) {
        return validationRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Validación no encontrada: " + id));
    }

    private String generateRequestId(CreateValidationRequest req, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            return idempotencyKey;
        }

        String raw = req.customerDocument() + "|" + req.bankCode() + "|" + req.accountNumber();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generando request_id", e);
        }
    }
}