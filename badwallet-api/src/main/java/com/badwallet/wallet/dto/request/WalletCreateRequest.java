package com.badwallet.wallet.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record WalletCreateRequest(

        @NotBlank(message = "Le numéro de téléphone est obligatoire")
        @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Numéro de téléphone invalide")
        String phoneNumber,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Email invalide")
        String email,

        @NotNull(message = "Le solde initial est obligatoire")
        @DecimalMin(value = "0.0", message = "Le solde initial ne peut pas être négatif")
        BigDecimal initialBalance,

        @NotBlank(message = "Le code du wallet est obligatoire")
        String code,

        @NotBlank(message = "La devise est obligatoire")
        String currency
) {
}
