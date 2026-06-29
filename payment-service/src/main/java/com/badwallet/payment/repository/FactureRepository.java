package com.badwallet.payment.repository;

import com.badwallet.payment.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FactureRepository extends JpaRepository<Facture, Long> {

    Optional<Facture> findByReference(String reference);

    List<Facture> findByWalletCodeAndMois(String walletCode, String mois);

    List<Facture> findByWalletCodeAndMoisAndUnite(String walletCode, String mois, String unite);

    @Query("SELECT f FROM Facture f WHERE f.walletCode = :walletCode " +
           "AND f.createdAt BETWEEN :debut AND :fin")
    List<Facture> findByWalletCodeAndPeriode(
            @Param("walletCode") String walletCode,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin
    );

    List<Facture> findByReferenceIn(List<String> references);

    boolean existsByReference(String reference);
}
