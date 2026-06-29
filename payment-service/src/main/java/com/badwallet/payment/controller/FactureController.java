package com.badwallet.payment.controller;

import com.badwallet.payment.dto.request.PayFactureRequest;
import com.badwallet.payment.dto.response.FactureResponseDTO;
import com.badwallet.payment.dto.response.PageResponse;
import com.badwallet.payment.dto.response.RestResponse;
import com.badwallet.payment.service.FactureService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/factures")
public class FactureController {

    private final FactureService factureService;

    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    // GET /api/v1/factures?page=0&size=10
    @GetMapping
    public RestResponse<PageResponse<FactureResponseDTO>> getAllFactures(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<FactureResponseDTO> facturesPage = factureService.getAllFactures(pageable);
        PageResponse<FactureResponseDTO> pageResponse = PageResponse.of(facturesPage);
        return RestResponse.success(pageResponse, "Liste des factures récupérée avec succès");
    }

    // GET /api/v1/factures/{walletCode}/current
    @GetMapping("/{walletCode}/current")
    public RestResponse<List<FactureResponseDTO>> getFacturesCurrent(
            @PathVariable String walletCode,
            @RequestParam(required = false) String unite) {

        if (unite != null && !unite.isBlank()) {
            List<FactureResponseDTO> factures = factureService.getFacturesByUnite(walletCode, unite);
            return RestResponse.success(factures,
                    "Factures du mois en cours filtrées par unité (" + unite + ") récupérées avec succès");
        }

        List<FactureResponseDTO> factures = factureService.getFacturesCurrent(walletCode);
        return RestResponse.success(factures, "Factures du mois en cours récupérées avec succès");
    }

    // GET /api/v1/factures/{walletCode}/periode?debut=2026-05-01&fin=2026-07-01
    @GetMapping("/{walletCode}/periode")
    public RestResponse<List<FactureResponseDTO>> getFacturesByPeriode(
            @PathVariable String walletCode,
            @RequestParam String debut,
            @RequestParam String fin) {

        List<FactureResponseDTO> factures = factureService.getFacturesByPeriode(walletCode, debut, fin);
        return RestResponse.success(factures,
                "Factures sur la période " + debut + " → " + fin + " récupérées avec succès");
    }

    // POST /api/v1/factures/{reference}/pay
    @PostMapping("/{reference}/pay")
    @ResponseStatus(HttpStatus.OK)
    public RestResponse<FactureResponseDTO> payFacture(
            @PathVariable String reference,
            @Valid @RequestBody PayFactureRequest request) {

        FactureResponseDTO facture = factureService.payFacture(reference, request.amount());
        return RestResponse.success(facture, "Facture " + reference + " payée avec succès");
    }
}
