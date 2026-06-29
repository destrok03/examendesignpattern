package com.badwallet.wallet.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record PayRequest(

        @NotBlank(message = "Le numéro de téléphone est obligatoire")
        String phoneNumber,

        @NotBlank(message = "Le nom du service est obligatoire")
        String serviceName,

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "1.0", message = "Le montant doit être supérieur à 0")
        BigDecimal amount
) {
}
