package com.badwallet.wallet.adapter;

import com.badwallet.wallet.dto.response.FactureDTO;
import com.badwallet.wallet.exception.PaymentServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class PaymentServiceHttpAdapter implements IPaymentServiceAdapter {

    private final RestTemplate restTemplate;
    private final String paymentServiceUrl;

    public PaymentServiceHttpAdapter(
            RestTemplate restTemplate,
            @Value("${payment.service.url}") String paymentServiceUrl) {
        this.restTemplate = restTemplate;
        this.paymentServiceUrl = paymentServiceUrl;
    }

    @Override
    public List<FactureDTO> getFacturesCurrent(String walletCode) {
        String url = paymentServiceUrl + "/api/v1/factures/" + walletCode + "/current";
        try {
            ResponseEntity<RestResponseWrapper<List<FactureDTO>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return extractData(response);
        } catch (Exception ex) {
            throw new PaymentServiceException("Erreur lors de la récupération des factures : " + ex.getMessage());
        }
    }

    @Override
    public List<FactureDTO> getFacturesByUnite(String walletCode, String unite) {
        String url = paymentServiceUrl + "/api/v1/factures/" + walletCode + "/current?unite=" + unite;
        try {
            ResponseEntity<RestResponseWrapper<List<FactureDTO>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return extractData(response);
        } catch (Exception ex) {
            throw new PaymentServiceException("Erreur lors de la récupération des factures par unité : " + ex.getMessage());
        }
    }

    @Override
    public List<FactureDTO> getFacturesByPeriode(String walletCode, String debut, String fin) {
        String url = paymentServiceUrl + "/api/v1/factures/" + walletCode
                + "/periode?debut=" + debut + "&fin=" + fin;
        try {
            ResponseEntity<RestResponseWrapper<List<FactureDTO>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {}
            );
            return extractData(response);
        } catch (Exception ex) {
            throw new PaymentServiceException("Erreur lors de la récupération des factures par période : " + ex.getMessage());
        }
    }

    @Override
    public boolean payFacture(String reference, BigDecimal amount) {
        String url = paymentServiceUrl + "/api/v1/factures/" + reference + "/pay";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, BigDecimal> body = Map.of("amount", amount);
            HttpEntity<Map<String, BigDecimal>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<RestResponseWrapper<FactureDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
            return response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().success();
        } catch (Exception ex) {
            throw new PaymentServiceException("Erreur lors du paiement de la facture " + reference + " : " + ex.getMessage());
        }
    }

    private <T> T extractData(ResponseEntity<RestResponseWrapper<T>> response) {
        if (response.getBody() == null) {
            throw new PaymentServiceException("Réponse vide du payment-service");
        }
        return response.getBody().data();
    }

    // Record interne pour désérialiser la réponse RestResponse du payment-service
    private record RestResponseWrapper<T>(
            boolean success,
            int status,
            String message,
            T data
    ) {}
}
