package com.karpay.ach_validation.web;

import com.karpay.ach_validation.dto.LoginRequest;
import com.karpay.ach_validation.dto.TokenResponse;
import com.karpay.ach_validation.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;
    private final PasswordEncoder encoder;

    private final Map<String, String[]> users;

    public AuthController(JwtService jwtService, PasswordEncoder encoder) {
        this.jwtService = jwtService;
        this.encoder = encoder;
        this.users = Map.of(
                "admin",   new String[]{encoder.encode("admin123"),   "ADMIN"},
                "fintech", new String[]{encoder.encode("fintech123"), "FINTECH"},
                "auditor", new String[]{encoder.encode("auditor123"), "AUDITOR"}
        );
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        String[] data = users.get(req.username());

        if (data == null || !encoder.matches(req.password(), data[0])) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String role = data[1];
        String token = jwtService.generateToken(req.username(), role);
        return ResponseEntity.ok(new TokenResponse(token));
    }
}