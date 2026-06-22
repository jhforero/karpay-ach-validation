package com.karpay.ach_validation.client;

import com.karpay.ach_validation.dto.AchValidateRequest;
import com.karpay.ach_validation.dto.AchValidateResponse;
import com.karpay.ach_validation.exception.AchUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AchClient {

    private static final Logger log = LoggerFactory.getLogger(AchClient.class);
    private static final String ACH = "achService";

    private final WebClient achWebClient;

    public AchClient(WebClient achWebClient) {
        this.achWebClient = achWebClient;
    }

    @CircuitBreaker(name = ACH, fallbackMethod = "fallback")
    @Retry(name = ACH)
    public AchValidateResponse validate(AchValidateRequest request) {
        log.info("Llamando a ACH para cuenta {}", request.accountNumber());
        return achWebClient.post()
                .uri("/ach/validate")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AchValidateResponse.class)
                .block();
    }

    private AchValidateResponse fallback(AchValidateRequest request, Throwable t) {
        log.error("ACH no disponible para cuenta {}: {}", request.accountNumber(), t.toString());
        throw new AchUnavailableException(
                "El servicio ACH no está disponible. Intente más tarde.");
    }
}