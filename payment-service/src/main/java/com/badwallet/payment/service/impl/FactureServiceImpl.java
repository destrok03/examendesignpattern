package com.badwallet.payment.service.impl;

import com.badwallet.payment.dto.response.FactureResponseDTO;
import com.badwallet.payment.entity.Facture;
import com.badwallet.payment.exception.BusinessException;
import com.badwallet.payment.exception.EntityNotFoundException;
import com.badwallet.payment.mapper.FactureMapper;
import com.badwallet.payment.repository.FactureRepository;
import com.badwallet.payment.service.FactureService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Transactional
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;
    private final FactureMapper factureMapper;

    public FactureServiceImpl(FactureRepository factureRepository, FactureMapper factureMapper) {
        this.factureRepository = factureRepository;
        this.factureMapper = factureMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FactureResponseDTO> getFacturesCurrent(String walletCode) {
        // Mois en cours au format yyyy-MM
        String moisCourant = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return factureRepository.findByWalletCodeAndMois(walletCode, moisCourant)
                .stream()
                .map(factureMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FactureResponseDTO> getFacturesByUnite(String walletCode, String unite) {
        String moisCourant = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        return factureRepository.findByWalletCodeAndMoisAndUnite(walletCode, moisCourant, unite)
                .stream()
                .map(factureMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FactureResponseDTO> getFacturesByPeriode(String walletCode, String debut, String fin) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime debutDate = LocalDate.parse(debut, formatter).atStartOfDay();
        LocalDateTime finDate = LocalDate.parse(fin, formatter).atTime(23, 59, 59);

        return factureRepository.findByWalletCodeAndPeriode(walletCode, debutDate, finDate)
                .stream()
                .map(factureMapper::toDTO)
                .toList();
    }

    @Override
    public FactureResponseDTO payFacture(String reference, BigDecimal amount) {
        Facture facture = factureRepository.findByReference(reference)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Facture introuvable avec la référence : " + reference));

        if (facture.getStatut() == Facture.StatutFacture.PAID) {
            throw new BusinessException("La facture " + reference + " est déjà payée");
        }

        if (amount.compareTo(facture.getMontant()) < 0) {
            throw new BusinessException(
                    "Montant insuffisant. Montant de la facture : " + facture.getMontant() + " CFA");
        }

        facture.setStatut(Facture.StatutFacture.PAID);
        facture.setPayedAt(LocalDateTime.now());
        Facture saved = factureRepository.save(facture);

        return factureMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FactureResponseDTO> getAllFactures(Pageable pageable) {
        return factureRepository.findAll(pageable)
                .map(factureMapper::toDTO);
    }
}
