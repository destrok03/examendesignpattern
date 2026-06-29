package com.badwallet.payment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "factures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private String walletCode;

    @Column(nullable = false)
    private String unite;

    @Column(nullable = false)
    private String mois;

    @Column(nullable = false)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutFacture statut;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime payedAt;

    public enum StatutFacture {
        UNPAID,
        PAID
    }

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.statut == null) {
            this.statut = StatutFacture.UNPAID;
        }
    }
}
