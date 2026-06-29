package com.badwallet.payment.service;

import com.badwallet.payment.dto.response.FactureResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface FactureService {

    // GET /factures/{walletCode}/current
    List<FactureResponseDTO> getFacturesCurrent(String walletCode);

    // GET /factures/{walletCode}/current?unite=WOYAFAL
    List<FactureResponseDTO> getFacturesByUnite(String walletCode, String unite);

    // GET /factures/{walletCode}/periode?debut=...&fin=...
    List<FactureResponseDTO> getFacturesByPeriode(String walletCode, String debut, String fin);

    // POST /factures/{reference}/pay
    FactureResponseDTO payFacture(String reference, BigDecimal amount);

    // GET /factures (paginé - pour consultation admin)
    Page<FactureResponseDTO> getAllFactures(Pageable pageable);
}
