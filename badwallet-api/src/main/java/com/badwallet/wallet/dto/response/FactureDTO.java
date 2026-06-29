package com.badwallet.wallet.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FactureDTO(
        Long id,
        String reference,
        String walletCode,
        String unite,
        String mois,
        BigDecimal montant,
        String statut,
        LocalDateTime createdAt,
        LocalDateTime payedAt
) {
}
