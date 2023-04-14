package com.paypay.dto.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ResponseInquiryPayment {
    
    private BigDecimal idPayment;

    private BigDecimal idTransaction;
    
    private String vaNumber;
    
    private BigDecimal totalAmount;
    
    private String sourceAccount;

    private String currency;

    private String destinationAccount;
    
    private String bankPayment;

    private String nic;

    private String paymentStatus;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdate;
}
