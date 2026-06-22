package com.karpay.ach_validation.web;
import com.karpay.ach_validation.dto.AchValidateRequest;
import com.karpay.ach_validation.dto.AchValidateResponse;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/ach")
public class AchSimulatorController {

    @PostMapping("/validate")
    public AchValidateResponse validate(@RequestBody AchValidateRequest request) {
        String reference = "ACH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new AchValidateResponse(reference, "PROCESSING");
    }
}