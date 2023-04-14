package com.paypay.dto.Request;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class TransferRequest {
    
    @NotEmpty(message = "Field must be fill")
    private String vaNumber;
    
    @NotEmpty(message = "Field must be fill")
    private String sourceAccount;
    @NotEmpty(message = "Field must be fill")
    private String bankName;

}
