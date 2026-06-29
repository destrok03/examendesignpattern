package com.badwallet.wallet.dto.request;

import jakarta.validation.constraints.*;
import java.util.List;

public record PayFacturesRequest(

        @NotBlank(message = "Le numéro de téléphone est obligatoire")
        String phoneNumber,

        @NotBlank(message = "Le nom du service est obligatoire")
        String serviceName,

        @NotNull(message = "La liste des références est obligatoire")
        @Size(min = 1, message = "Au moins une référence de facture est requise")
        List<String> factureReferences
) {
}
