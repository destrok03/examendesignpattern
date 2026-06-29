package com.badwallet.wallet.service.impl;

import com.badwallet.wallet.adapter.IPaymentServiceAdapter;
import com.badwallet.wallet.dto.request.*;
import com.badwallet.wallet.dto.response.*;
import com.badwallet.wallet.entity.Transaction;
import com.badwallet.wallet.entity.Transaction.TransactionType;
import com.badwallet.wallet.entity.Wallet;
import com.badwallet.wallet.exception.*;
import com.badwallet.wallet.mapper.TransactionMapper;
import com.badwallet.wallet.mapper.WalletMapper;
import com.badwallet.wallet.repository.TransactionRepository;
import com.badwallet.wallet.repository.WalletRepository;
import com.badwallet.wallet.service.WalletService;
import com.badwallet.wallet.strategy.FeeStrategyFactory;
import com.badwallet.wallet.strategy.IFeeStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final IPaymentServiceAdapter paymentAdapter;
    private final FeeStrategyFactory feeStrategyFactory;
    private final WalletMapper walletMapper;
    private final TransactionMapper transactionMapper;

    public WalletServiceImpl(
            WalletRepository walletRepository,
            TransactionRepository transactionRepository,
            IPaymentServiceAdapter paymentAdapter,
            FeeStrategyFactory feeStrategyFactory,
            WalletMapper walletMapper,
            TransactionMapper transactionMapper) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
        this.paymentAdapter = paymentAdapter;
        this.feeStrategyFactory = feeStrategyFactory;
        this.walletMapper = walletMapper;
        this.transactionMapper = transactionMapper;
    }

    // ─── CREATE ────────────────────────────────────────────────
    @Override
    public WalletResponseDTO createWallet(WalletCreateRequest request) {
        if (walletRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new EntityExistsException(
                    "Un wallet avec le téléphone " + request.phoneNumber() + " existe déjà");
        }
        if (walletRepository.existsByCode(request.code())) {
            throw new EntityExistsException(
                    "Un wallet avec le code " + request.code() + " existe déjà");
        }

        Wallet wallet = Wallet.builder()
                .code(request.code())
                .phoneNumber(request.phoneNumber())
                .email(request.email())
                .balance(request.initialBalance())
                .currency(request.currency())
                .build();

        Wallet saved = walletRepository.save(wallet);
        return walletMapper.toDTO(saved);
    }

    // ─── GET ALL ────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public Page<WalletResponseDTO> getAllWallets(Pageable pageable) {
        return walletRepository.findAll(pageable).map(walletMapper::toDTO);
    }

    // ─── GET BY PHONE ────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public WalletResponseDTO getWalletByPhone(String phoneNumber) {
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet introuvable avec le téléphone : " + phoneNumber));
        return walletMapper.toDTO(wallet);
    }

    // ─── GET BALANCE ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public BalanceResponseDTO getBalance(String phoneNumber) {
        Wallet wallet = walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet introuvable avec le téléphone : " + phoneNumber));
        return BalanceResponseDTO.builder()
                .phoneNumber(wallet.getPhoneNumber())
                .code(wallet.getCode())
                .balance(wallet.getBalance())
                .currency(wallet.getCurrency())
                .build();
    }

    // ─── DEPOSIT ─────────────────────────────────────────────────
    @Override
    public TransactionResponseDTO deposit(Long id, DepositRequest request) {
        Wallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Wallet introuvable avec l'id : " + id));

        // Factory → Strategy (NoFeeStrategy : frais = 0 pour un dépôt)
        IFeeStrategy strategy = feeStrategyFactory.getStrategy(TransactionType.CREDIT);
        BigDecimal fees = strategy.calculate(request.amount());

        wallet.credit(request.amount());
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .type(TransactionType.CREDIT)
                .amount(request.amount())
                .fees(fees)
                .description("Dépôt via " + request.paymentMethod())
                .wallet(wallet)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toDTO(saved);
    }

    // ─── WITHDRAW ────────────────────────────────────────────────
    @Override
    public TransactionResponseDTO withdraw(WithdrawRequest request) {
        Wallet wallet = walletRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet introuvable avec le téléphone : " + request.phoneNumber()));

        // Factory → Strategy (WithdrawFeeStrategy : frais 1%, max 5000 CFA)
        IFeeStrategy strategy = feeStrategyFactory.getStrategy(TransactionType.DEBIT);
        BigDecimal fees = strategy.calculate(request.amount());
        BigDecimal totalDebit = request.amount().add(fees);

        if (!wallet.hasSufficientBalance(totalDebit)) {
            throw new InsufficientBalanceException(
                    "Solde insuffisant. Solde disponible : " + wallet.getBalance()
                            + " CFA. Montant requis (frais inclus) : " + totalDebit + " CFA");
        }

        wallet.debit(totalDebit);
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .type(TransactionType.DEBIT)
                .amount(request.amount())
                .fees(fees)
                .description("Retrait — frais : " + fees + " CFA")
                .wallet(wallet)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toDTO(saved);
    }

    // ─── TRANSFER ────────────────────────────────────────────────
    @Override
    public TransactionResponseDTO transfer(TransferRequest request) {
        Wallet sender = walletRepository.findByPhoneNumber(request.senderPhone())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet expéditeur introuvable : " + request.senderPhone()));

        Wallet receiver = walletRepository.findByPhoneNumber(request.receiverPhone())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet destinataire introuvable : " + request.receiverPhone()));

        // Factory → Strategy (NoFeeStrategy : transfert interne gratuit)
        IFeeStrategy strategy = feeStrategyFactory.getStrategy(TransactionType.TRANSFER);
        BigDecimal fees = strategy.calculate(request.amount());

        if (!sender.hasSufficientBalance(request.amount())) {
            throw new InsufficientBalanceException(
                    "Solde insuffisant pour le transfert. Solde disponible : " + sender.getBalance() + " CFA");
        }

        sender.debit(request.amount());
        receiver.credit(request.amount());

        walletRepository.save(sender);
        walletRepository.save(receiver);

        // Transaction côté expéditeur
        Transaction txSender = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(request.amount())
                .fees(fees)
                .description("Transfert vers " + request.receiverPhone())
                .wallet(sender)
                .build();

        // Transaction côté destinataire
        Transaction txReceiver = Transaction.builder()
                .type(TransactionType.TRANSFER)
                .amount(request.amount())
                .fees(BigDecimal.ZERO)
                .description("Transfert reçu de " + request.senderPhone())
                .wallet(receiver)
                .build();

        transactionRepository.save(txSender);
        transactionRepository.save(txReceiver);

        return transactionMapper.toDTO(txSender);
    }

    // ─── PAY CURRENT BILL ────────────────────────────────────────
    @Override
    public TransactionResponseDTO payCurrentBill(PayRequest request) {
        Wallet wallet = walletRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet introuvable avec le téléphone : " + request.phoneNumber()));

        if (!wallet.hasSufficientBalance(request.amount())) {
            throw new InsufficientBalanceException(
                    "Solde insuffisant. Solde disponible : " + wallet.getBalance() + " CFA");
        }

        // Récupère les factures impayées du mois en cours via l'Adapter
        var factures = paymentAdapter.getFacturesCurrent(wallet.getCode());

        var facture = factures.stream()
                .filter(f -> f.statut().equals("UNPAID")
                        && f.unite().equalsIgnoreCase(request.serviceName()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucune facture impayée trouvée pour le service : " + request.serviceName()));

        // Paiement via l'Adapter (appel HTTP vers payment-service:8081)
        boolean success = paymentAdapter.payFacture(facture.reference(), request.amount());
        if (!success) {
            throw new PaymentServiceException("Le paiement de la facture a échoué côté payment-service");
        }

        wallet.debit(request.amount());
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .type(TransactionType.PAYMENT)
                .amount(request.amount())
                .fees(BigDecimal.ZERO)
                .description("Paiement facture " + request.serviceName() + " — réf : " + facture.reference())
                .wallet(wallet)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return transactionMapper.toDTO(saved);
    }

    // ─── PAY SPECIFIC BILLS ──────────────────────────────────────
    @Override
    public List<TransactionResponseDTO> paySpecificBills(PayFacturesRequest request) {
        Wallet wallet = walletRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet introuvable avec le téléphone : " + request.phoneNumber()));

        // Récupère les factures du mois par leur référence via l'Adapter
        var factures = paymentAdapter.getFacturesCurrent(wallet.getCode());

        var facturesCibles = factures.stream()
                .filter(f -> request.factureReferences().contains(f.reference())
                        && f.statut().equals("UNPAID"))
                .toList();

        if (facturesCibles.isEmpty()) {
            throw new EntityNotFoundException("Aucune facture impayée trouvée pour les références données");
        }

        // Vérification du solde total
        BigDecimal totalAmount = facturesCibles.stream()
                .map(FactureDTO::montant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!wallet.hasSufficientBalance(totalAmount)) {
            throw new InsufficientBalanceException(
                    "Solde insuffisant pour payer toutes les factures. Total requis : " + totalAmount + " CFA");
        }

        List<TransactionResponseDTO> results = new ArrayList<>();

        for (var facture : facturesCibles) {
            boolean success = paymentAdapter.payFacture(facture.reference(), facture.montant());
            if (!success) {
                throw new PaymentServiceException(
                        "Échec du paiement pour la facture : " + facture.reference());
            }

            wallet.debit(facture.montant());
            walletRepository.save(wallet);

            Transaction transaction = Transaction.builder()
                    .type(TransactionType.PAYMENT)
                    .amount(facture.montant())
                    .fees(BigDecimal.ZERO)
                    .description("Paiement facture " + request.serviceName() + " — réf : " + facture.reference())
                    .wallet(wallet)
                    .build();

            Transaction saved = transactionRepository.save(transaction);
            results.add(transactionMapper.toDTO(saved));
        }

        return results;
    }

    // ─── GET TRANSACTIONS ────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactions(String phoneNumber) {
        walletRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Wallet introuvable avec le téléphone : " + phoneNumber));

        return transactionRepository
                .findByWalletPhoneNumberOrderByCreatedAtDesc(phoneNumber)
                .stream()
                .map(transactionMapper::toDTO)
                .toList();
    }

    // ─── SEED DATABASE (@Async) ──────────────────────────────────
    @Override
    @Async
    public void seedDatabase(int numWallets, int eventsPerWallet) {
        Random random = new Random();
        String mois = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        for (int i = 1; i <= numWallets; i++) {
            String phone = "+22177000" + String.format("%04d", i);
            String code  = "WLT-" + String.format("%07d", i);

            if (walletRepository.existsByPhoneNumber(phone)) {
                continue;
            }

            Wallet wallet = Wallet.builder()
                    .code(code)
                    .phoneNumber(phone)
                    .email("wallet" + i + "@badwallet.sn")
                    .balance(new BigDecimal(random.nextInt(500000) + 10000))
                    .currency("XOF")
                    .build();

            walletRepository.save(wallet);

            for (int j = 0; j < eventsPerWallet; j++) {
                TransactionType[] types = TransactionType.values();
                TransactionType type = types[random.nextInt(types.length)];
                BigDecimal amount = new BigDecimal(random.nextInt(50000) + 1000);

                Transaction tx = Transaction.builder()
                        .type(type)
                        .amount(amount)
                        .fees(BigDecimal.ZERO)
                        .description("Transaction seed #" + j)
                        .wallet(wallet)
                        .build();

                transactionRepository.save(tx);
            }
        }

        System.out.println(">>> Seed terminé : " + numWallets
                + " wallets, " + (numWallets * eventsPerWallet) + " transactions.");
    }
}
