package com.badwallet.wallet.service;

import com.badwallet.wallet.dto.request.*;
import com.badwallet.wallet.dto.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WalletService {

    WalletResponseDTO createWallet(WalletCreateRequest request);

    Page<WalletResponseDTO> getAllWallets(Pageable pageable);

    WalletResponseDTO getWalletByPhone(String phoneNumber);

    BalanceResponseDTO getBalance(String phoneNumber);

    TransactionResponseDTO deposit(Long id, DepositRequest request);

    TransactionResponseDTO withdraw(WithdrawRequest request);

    TransactionResponseDTO transfer(TransferRequest request);

    TransactionResponseDTO payCurrentBill(PayRequest request);

    List<TransactionResponseDTO> paySpecificBills(PayFacturesRequest request);

    List<TransactionResponseDTO> getTransactions(String phoneNumber);

    void seedDatabase(int numWallets, int eventsPerWallet);
}
