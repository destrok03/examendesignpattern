package com.badwallet.wallet.controller;

import com.badwallet.wallet.adapter.IPaymentServiceAdapter;
import com.badwallet.wallet.dto.response.FactureDTO;
import com.badwallet.wallet.dto.response.RestResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/external/factures")
public class ExternalFactureController {

    private final IPaymentServiceAdapter paymentAdapter;

    public ExternalFactureController(IPaymentServiceAdapter paymentAdapter) {
        this.paymentAdapter = paymentAdapter;
    }

    // GET /api/v1/external/factures/{walletCode}/current
    // GET /api/v1/external/factures/{walletCode}/current?unite=WOYAFAL
    @GetMapping("/{walletCode}/current")
    public RestResponse<List<FactureDTO>> getFacturesCurrent(
            @PathVariable String walletCode,
            @RequestParam(required = false) String unite) {

        if (unite != null && !unite.isBlank()) {
            List<FactureDTO> factures = paymentAdapter.getFacturesByUnite(walletCode, unite);
            return RestResponse.success(factures,
                    "Factures filtrées par unité (" + unite + ") récupérées avec succès");
        }

        List<FactureDTO> factures = paymentAdapter.getFacturesCurrent(walletCode);
        return RestResponse.success(factures, "Factures du mois en cours récupérées avec succès");
    }

    // GET /api/v1/external/factures/{walletCode}/periode?debut=2026-05-01&fin=2026-07-01
    @GetMapping("/{walletCode}/periode")
    public RestResponse<List<FactureDTO>> getFacturesByPeriode(
            @PathVariable String walletCode,
            @RequestParam String debut,
            @RequestParam String fin) {

        List<FactureDTO> factures = paymentAdapter.getFacturesByPeriode(walletCode, debut, fin);
        return RestResponse.success(factures,
                "Factures sur la période " + debut + " → " + fin + " récupérées avec succès");
    }
}
