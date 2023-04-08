package com.paypay.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.paypay.model.UtilitiesAccount;

@Repository
public interface UtilitiesAccountRepo extends JpaRepository<UtilitiesAccount, BigDecimal> {
    UtilitiesAccount findByAccountNumberAndBankName(String accountNumber, String bankName);
}
