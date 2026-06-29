package com.badwallet.wallet.dto.response;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record WalletResponseDTO(
        Long id,
        String code,
        String phoneNumber,
        String email,
        BigDecimal balance,
        String currency,
        LocalDateTime createdAt
) {
}
