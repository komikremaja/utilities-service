package com.paypay.dto.Request;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReconPaymentStatusRequest {
    
    private BigDecimal idPayment;
    private String paymentStatus;
    private String sourceAccount;
    private String bankPayment;
}
