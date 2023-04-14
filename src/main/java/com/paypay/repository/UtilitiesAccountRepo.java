package com.paypay.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paypay.model.UtilitiesAccount;

@Repository
public interface UtilitiesAccountRepo extends JpaRepository<UtilitiesAccount, BigDecimal> {
    UtilitiesAccount findByAccountNumberAndBankNameAndCurrencyType(String accountNumber, String bankName, String currencyType);
    UtilitiesAccount findByAccountNumber(String accountNumber);
    UtilitiesAccount findByAccountNumberAndCurrencyType(String accountNumber, String currencyType);
    UtilitiesAccount findByAccountNumberAndBankName(String accountNumber, String bankname);
    
}
