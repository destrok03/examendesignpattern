package com.badwallet.wallet.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record TransferRequest(

        @NotBlank(message = "Le numéro de téléphone de l'expéditeur est obligatoire")
        String senderPhone,

        @NotBlank(message = "Le numéro de téléphone du destinataire est obligatoire")
        String receiverPhone,

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "1.0", message = "Le montant doit être supérieur à 0")
        BigDecimal amount
) {
}
