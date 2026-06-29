package com.badwallet.wallet.dto.response;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionResponseDTO(
        Long id,
        String type,
        BigDecimal amount,
        BigDecimal fees,
        String description,
        LocalDateTime createdAt,
        String walletCode
) {
}
