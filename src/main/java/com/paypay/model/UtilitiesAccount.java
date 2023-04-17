package com.paypay.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "utilities_account_data")
public class UtilitiesAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_account")
    private Integer idAccount;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_status")
    private String accountStatus;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "currency_type")
    private String currencyType;
    
    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
}
