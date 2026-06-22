package com.karpay.ach_validation.dto;

public record TokenResponse(String accessToken, String tokenType) {
    public TokenResponse(String accessToken) {
        this(accessToken, "Bearer");
    }
}