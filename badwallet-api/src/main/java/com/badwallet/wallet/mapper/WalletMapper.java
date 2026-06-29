package com.badwallet.wallet.mapper;

import com.badwallet.wallet.dto.response.WalletResponseDTO;
import com.badwallet.wallet.entity.Wallet;
import org.springframework.stereotype.Component;

@Component
public class WalletMapper {

    public WalletResponseDTO toDTO(Wallet wallet) {
        return WalletResponseDTO.builder()
                .id(wallet.getId())
                .code(wallet.getCode())
                .phoneNumber(wallet.getPhoneNumber())
                .email(wallet.getEmail())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .createdAt(wallet.getCreatedAt())
                .build();
    }
}
