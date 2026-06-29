package com.badwallet.payment.mock;

import com.badwallet.payment.entity.Facture;
import com.badwallet.payment.repository.FactureRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
public class FactureMock implements CommandLineRunner {

    private final FactureRepository factureRepository;

    public FactureMock(FactureRepository factureRepository) {
        this.factureRepository = factureRepository;
    }

    @Override
    public void run(String... args) {
        if (factureRepository.count() != 0) {
            return;
        }

        String moisCourant = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String moisPrecedent = LocalDate.now().minusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM"));

        List<Facture> factures = new ArrayList<>();

        // Factures pour WLT-0000001
        factures.add(buildFacture("FAC-ISM-1-1",   "WLT-0000001", "ISM",     moisCourant,  new BigDecimal("150000")));
        factures.add(buildFacture("FAC-WOYA-1-1",  "WLT-0000001", "WOYAFAL", moisCourant,  new BigDecimal("25000")));
        factures.add(buildFacture("FAC-WOYA-1-2",  "WLT-0000001", "WOYAFAL", moisPrecedent, new BigDecimal("22000")));

        // Factures pour WLT-0000002
        factures.add(buildFacture("FAC-ISM-2-1",   "WLT-0000002", "ISM",     moisCourant,  new BigDecimal("150000")));
        factures.add(buildFacture("FAC-WOYA-2-1",  "WLT-0000002", "WOYAFAL", moisCourant,  new BigDecimal("30000")));

        // Factures pour WLT-0000003
        factures.add(buildFacture("FAC-ISM-3-1",   "WLT-0000003", "ISM",     moisCourant,  new BigDecimal("150000")));
        factures.add(buildFacture("FAC-ISM-3-2",   "WLT-0000003", "ISM",     moisPrecedent, new BigDecimal("150000")));
        factures.add(buildFacture("FAC-ISM-3-3",   "WLT-0000003", "ISM",     moisCourant,  new BigDecimal("50000")));
        factures.add(buildFacture("FAC-WOYA-3-1",  "WLT-0000003", "WOYAFAL", moisCourant,  new BigDecimal("18000")));

        factureRepository.saveAll(factures);
        System.out.println(">>> FactureMock : " + factures.size() + " factures insérées.");
    }

    private Facture buildFacture(String reference, String walletCode,
                                  String unite, String mois, BigDecimal montant) {
        return Facture.builder()
                .reference(reference)
                .walletCode(walletCode)
                .unite(unite)
                .mois(mois)
                .montant(montant)
                .statut(Facture.StatutFacture.UNPAID)
                .build();
    }
}
