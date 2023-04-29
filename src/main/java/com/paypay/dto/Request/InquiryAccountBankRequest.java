package com.paypay.dto.Request;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class InquiryAccountBankRequest {
    
    @NotEmpty(message = "Field must be fill")
    private String accountNumber;
    
    @NotEmpty(message = "Field must be fill")
    private String bankName;
    
    @NotEmpty(message = "Field must be fill")
    private String accountType;
}
