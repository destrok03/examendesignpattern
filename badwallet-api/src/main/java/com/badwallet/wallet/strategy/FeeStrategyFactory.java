package com.badwallet.wallet.strategy;

import com.badwallet.wallet.entity.Transaction.TransactionType;

public class FeeStrategyFactory {

    public IFeeStrategy getStrategy(TransactionType type) {
        return switch (type) {
            case DEBIT    -> new WithdrawFeeStrategy();
            case CREDIT   -> new NoFeeStrategy();
            case TRANSFER -> new NoFeeStrategy();
            case PAYMENT  -> new NoFeeStrategy();
        };
    }
}
