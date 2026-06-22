package com.karpay.ach_validation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateValidationRequest(

        @NotBlank(message = "El documento es obligatorio")
        String customerDocument,

        @NotBlank(message = "Codigo del banco es obligatorio")
        String bankCode,

        @NotBlank(message = "Tipo de cuenta es obligatorio")
        @Pattern(regexp = "SAVINGS|CHECKING", message = "Tipo de cuenta debe ser SAVINGS o CHECKING")
        String accountType,

        @NotBlank(message = "Numero de ucenta es obligatorio")
        String accountNumber
) {}