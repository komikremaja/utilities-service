package com.paypay.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.paypay.Exception.BadRequestException;
import com.paypay.constant.VariableConstant;
import com.paypay.dto.Request.InquiryAccountBankRequest;
import com.paypay.dto.Request.InquiryNpwpRequest;
import com.paypay.dto.Response.ResponseData;
import com.paypay.dto.Response.ResponseNpwp;
import com.paypay.model.UtilitiesAccount;
import com.paypay.model.UtilitiesNpwp;
import com.paypay.repository.UtilitiesAccountRepo;
import com.paypay.repository.UtilitiesNpwpRepo;
import com.paypay.dto.Response.Response;
import com.paypay.dto.Response.ResponseAccount;

@Service
public class UtilitiesImpl {

    private Response response;

    @Autowired
    private UtilitiesNpwpRepo utilitiesNpwpRepo;

    @Autowired
    private UtilitiesAccountRepo utilitiesAccountRepo;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private VariableConstant constantVar;

    public Response inquiryDataNpwp(InquiryNpwpRequest inquiryNpwpRequest) throws Exception {
        ResponseNpwp responseNpwp = new ResponseNpwp();
        // try {
            UtilitiesNpwp npwpDb = utilitiesNpwpRepo.findByNpwpNumber(inquiryNpwpRequest.getNpwp());
            if (npwpDb == null) {
                throw new BadRequestException("Data NPWP not found");
            }
            if (!npwpDb.getNpwpStatus().equalsIgnoreCase("actived")) {
                throw new BadRequestException("Data NPWP is not actived!");
            }
            responseNpwp = mapper.map(npwpDb, responseNpwp.getClass());
        // } catch (Exception e) {
        //     // TODO: handle exception
        //     responseData.setMessage(e.getMessage());
        //     responseData.setStatus(false);
        //     return response = new Response(constantVar.getSTATUS_FAILED(), "Failed", responseData);
        // }
        return response = new Response(constantVar.getSTATUS_OK(), "Success", responseNpwp);
    }

    public Response inquiryDataAccountBank(InquiryAccountBankRequest request) throws Exception {
        ResponseAccount responseAccount = new ResponseAccount();
        UtilitiesAccount accountDb = utilitiesAccountRepo.findByAccountNumberAndBankName(request.getAccountNumber(),
                request.getBankName());
        if (accountDb == null) {
            throw new BadRequestException("Data Rekening not found");
        }
        if (!accountDb.getAccountStatus().equalsIgnoreCase("actived")) {
            throw new BadRequestException("Data Rekening is not actived!");
        }
        responseAccount = mapper.map(accountDb, responseAccount.getClass());
        // } catch (Exception e) {
        // // TODO: handle exception
        // responseData.setMessage(e.getMessage());
        // responseData.setStatus(false);
        // return response = new Response(constantVar.getSTATUS_FAILED(), "Failed",
        // responseData);
        // }
        return response = new Response(constantVar.getSTATUS_OK(), "Success", responseAccount);
    }
}
