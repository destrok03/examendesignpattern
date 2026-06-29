package com.badwallet.wallet.adapter;

import com.badwallet.wallet.dto.response.FactureDTO;

import java.math.BigDecimal;
import java.util.List;

public interface IPaymentServiceAdapter {

    List<FactureDTO> getFacturesCurrent(String walletCode);

    List<FactureDTO> getFacturesByUnite(String walletCode, String unite);

    List<FactureDTO> getFacturesByPeriode(String walletCode, String debut, String fin);

    boolean payFacture(String reference, BigDecimal amount);
}
