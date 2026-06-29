package com.badwallet.payment.mapper;

import com.badwallet.payment.dto.response.FactureResponseDTO;
import com.badwallet.payment.entity.Facture;
import org.springframework.stereotype.Component;

@Component
public class FactureMapper {

    public FactureResponseDTO toDTO(Facture facture) {
        return FactureResponseDTO.builder()
                .id(facture.getId())
                .reference(facture.getReference())
                .walletCode(facture.getWalletCode())
                .unite(facture.getUnite())
                .mois(facture.getMois())
                .montant(facture.getMontant())
                .statut(facture.getStatut().name())
                .createdAt(facture.getCreatedAt())
                .payedAt(facture.getPayedAt())
                .build();
    }
}
