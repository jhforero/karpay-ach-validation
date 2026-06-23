package com.karpay.ach_validation.web;

import com.karpay.ach_validation.dto.AchValidateRequest;
import com.karpay.ach_validation.dto.AchValidateResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/ach")
public class AchSimulatorController {

    // Bandera para forzar fallos durante las pruebas de resiliencia.
    // Por defecto false: el simulador responde normal.
    private volatile boolean failMode = false;

    @PostMapping("/validate")
    public AchValidateResponse validate(@RequestBody AchValidateRequest request) throws InterruptedException {

        // Si está en modo fallo, simulamos un error del servicio externo
        if (failMode) {
            // Opción A: error 500 (dispara Retry y, si se repite, el Circuit Breaker)
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ACH caido (simulado)");

            // Opción B: en vez de la línea de arriba, para probar TIMEOUT,
            // comenta el throw y descomenta esto (que tarde más que tu timeout):
            // Thread.sleep(10000); // 10 segundos
        }

        String reference = "ACH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new AchValidateResponse(reference, "PROCESSING");
    }

    // Endpoint para encender/apagar el modo fallo sin reiniciar la app.
    // Ej: POST /ach/fail-mode?enabled=true
    @PostMapping("/fail-mode")
    public String setFailMode(@RequestParam boolean enabled) {
        this.failMode = enabled;
        return "failMode = " + this.failMode;
    }
}