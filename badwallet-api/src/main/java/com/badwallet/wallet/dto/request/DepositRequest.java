package com.badwallet.wallet.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record DepositRequest(

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "1.0", message = "Le montant doit être supérieur à 0")
        BigDecimal amount,

        @NotBlank(message = "La méthode de paiement est obligatoire")
        String paymentMethod
) {
}
