package com.badwallet.wallet.mapper;

import com.badwallet.wallet.dto.response.TransactionResponseDTO;
import com.badwallet.wallet.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponseDTO toDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .type(transaction.getType().name())
                .amount(transaction.getAmount())
                .fees(transaction.getFees())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .walletCode(transaction.getWallet().getCode())
                .build();
    }
}
