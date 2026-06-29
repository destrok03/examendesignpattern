package com.badwallet.wallet.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class WithdrawFeeStrategy implements IFeeStrategy {

    private static final BigDecimal RATE = new BigDecimal("0.01");
    private static final BigDecimal MAX_FEE = new BigDecimal("5000");

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        BigDecimal fees = amount.multiply(RATE).setScale(2, RoundingMode.HALF_UP);
        return fees.min(MAX_FEE);
    }
}
