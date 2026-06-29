package com.badwallet.wallet.strategy;

import java.math.BigDecimal;

public interface IFeeStrategy {
    BigDecimal calculate(BigDecimal amount);
}
