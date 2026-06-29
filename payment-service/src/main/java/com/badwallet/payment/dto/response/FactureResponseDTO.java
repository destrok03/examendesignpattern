package com.badwallet.payment.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record FactureResponseDTO(
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
