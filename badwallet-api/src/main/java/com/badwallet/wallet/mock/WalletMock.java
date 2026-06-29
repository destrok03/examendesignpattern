package com.badwallet.wallet.mock;

import com.badwallet.wallet.entity.Wallet;
import com.badwallet.wallet.repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Order(1)
public class WalletMock implements CommandLineRunner {

    private final WalletRepository walletRepository;

    public WalletMock(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    public void run(String... args) {
        if (walletRepository.count() != 0) {
            return;
        }

        Wallet w1 = Wallet.builder()
                .code("WLT-0000001")
                .phoneNumber("+221770000001")
                .email("client1@badwallet.sn")
                .balance(new BigDecimal("250000"))
                .currency("XOF")
                .build();

        Wallet w2 = Wallet.builder()
                .code("WLT-0000002")
                .phoneNumber("+221770000002")
                .email("client2@badwallet.sn")
                .balance(new BigDecimal("150000"))
                .currency("XOF")
                .build();

        Wallet w3 = Wallet.builder()
                .code("WLT-0000003")
                .phoneNumber("+221770000003")
                .email("client3@badwallet.sn")
                .balance(new BigDecimal("500000"))
                .currency("XOF")
                .build();

        walletRepository.save(w1);
        walletRepository.save(w2);
        walletRepository.save(w3);

        System.out.println(">>> WalletMock : 3 wallets insérés.");
    }
}
