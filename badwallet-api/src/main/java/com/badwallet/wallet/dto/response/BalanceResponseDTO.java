package com.badwallet.wallet.dto.response;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record BalanceResponseDTO(
        String phoneNumber,
        String code,
        BigDecimal balance,
        String currency
) {
}
