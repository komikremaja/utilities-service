package com.paypay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.paypay.dto.Request.InquiryAccountBankRequest;
import com.paypay.dto.Request.InquiryNpwpRequest;
import com.paypay.dto.Request.TransferRequest;
import com.paypay.dto.Response.Response;
import com.paypay.service.impl.UtilitiesImpl;

@RestController
@RequestMapping("/utilities-service")
public class UtilitiesController {
    private Response response;

    @Autowired
    private UtilitiesImpl utilitiesImpl;

    @PostMapping("/djp/inquiry-npwp")
    public Response inquiryNpwp(@RequestBody InquiryNpwpRequest request) throws Exception {
        response = utilitiesImpl.inquiryDataNpwp(request);
        return response;
    }

    @PostMapping("/bank/inquiry-rekening")
    public Response inquiryRekening(@RequestBody InquiryAccountBankRequest request) throws Exception {
        response = utilitiesImpl.inquiryDataAccountBank(request);
        return response;
    }

    @PostMapping("/bank/transfer")
    public Response transfer(@RequestBody TransferRequest request) throws Exception {
        response = utilitiesImpl.transfer(request);
        return response;
    }
}
