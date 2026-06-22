package com.karpay.ach_validation.dto;

public record AchValidateRequest(String bankCode, String accountType, String accountNumber) {}