package com.badwallet.wallet.controller;

import com.badwallet.wallet.dto.request.*;
import com.badwallet.wallet.dto.response.*;
import com.badwallet.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // POST /api/v1/wallets/seed?numWallets=10&eventsPerWallet=100
    @PostMapping("/seed")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public RestResponse<String> seed(
            @RequestParam(defaultValue = "10") int numWallets,
            @RequestParam(defaultValue = "100") int eventsPerWallet) {

        walletService.seedDatabase(numWallets, eventsPerWallet);
        return RestResponse.success(
                "Seeding en cours en arrière-plan...",
                "Requête acceptée",
                HttpStatus.ACCEPTED);
    }

    // POST /api/v1/wallets
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse<WalletResponseDTO> createWallet(
            @Valid @RequestBody WalletCreateRequest request) {

        WalletResponseDTO wallet = walletService.createWallet(request);
        return RestResponse.success(wallet, "Wallet créé avec succès", HttpStatus.CREATED);
    }

    // GET /api/v1/wallets?page=0&size=10
    @GetMapping
    public RestResponse<PageResponse<WalletResponseDTO>> getAllWallets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<WalletResponseDTO> walletsPage = walletService.getAllWallets(pageable);
        PageResponse<WalletResponseDTO> pageResponse = PageResponse.of(walletsPage);
        return RestResponse.success(pageResponse, "Liste des wallets récupérée avec succès");
    }

    // GET /api/v1/wallets/{phone}
    @GetMapping("/{phone}")
    public RestResponse<WalletResponseDTO> getWalletByPhone(@PathVariable String phone) {
        WalletResponseDTO wallet = walletService.getWalletByPhone(phone);
        return RestResponse.success(wallet, "Wallet récupéré avec succès");
    }

    // GET /api/v1/wallets/{phone}/balance
    @GetMapping("/{phone}/balance")
    public RestResponse<BalanceResponseDTO> getBalance(@PathVariable String phone) {
        BalanceResponseDTO balance = walletService.getBalance(phone);
        return RestResponse.success(balance, "Solde récupéré avec succès");
    }

    // POST /api/v1/wallets/{id}/deposit
    @PostMapping("/{id}/deposit")
    public RestResponse<TransactionResponseDTO> deposit(
            @PathVariable Long id,
            @Valid @RequestBody DepositRequest request) {

        TransactionResponseDTO tx = walletService.deposit(id, request);
        return RestResponse.success(tx, "Dépôt effectué avec succès");
    }

    // POST /api/v1/wallets/withdraw
    @PostMapping("/withdraw")
    public RestResponse<TransactionResponseDTO> withdraw(
            @Valid @RequestBody WithdrawRequest request) {

        TransactionResponseDTO tx = walletService.withdraw(request);
        return RestResponse.success(tx, "Retrait effectué avec succès");
    }

    // POST /api/v1/wallets/transfer
    @PostMapping("/transfer")
    public RestResponse<TransactionResponseDTO> transfer(
            @Valid @RequestBody TransferRequest request) {

        TransactionResponseDTO tx = walletService.transfer(request);
        return RestResponse.success(tx, "Transfert effectué avec succès");
    }

    // POST /api/v1/wallets/pay
    @PostMapping("/pay")
    public RestResponse<TransactionResponseDTO> payCurrentBill(
            @Valid @RequestBody PayRequest request) {

        TransactionResponseDTO tx = walletService.payCurrentBill(request);
        return RestResponse.success(tx, "Paiement de facture effectué avec succès");
    }

    // POST /api/v1/wallets/pay-factures
    @PostMapping("/pay-factures")
    public RestResponse<List<TransactionResponseDTO>> paySpecificBills(
            @Valid @RequestBody PayFacturesRequest request) {

        List<TransactionResponseDTO> txList = walletService.paySpecificBills(request);
        return RestResponse.success(txList, txList.size() + " facture(s) payée(s) avec succès");
    }

    // GET /api/v1/wallets/{phone}/transactions
    @GetMapping("/{phone}/transactions")
    public RestResponse<List<TransactionResponseDTO>> getTransactions(@PathVariable String phone) {
        List<TransactionResponseDTO> txList = walletService.getTransactions(phone);
        return RestResponse.success(txList, "Historique des transactions récupéré avec succès");
    }
}
