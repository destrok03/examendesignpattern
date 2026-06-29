package com.badwallet.wallet.strategy;

import java.math.BigDecimal;

public class NoFeeStrategy implements IFeeStrategy {

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return BigDecimal.ZERO;
    }
}
