package com.paypay.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.paypay.Exception.BadRequestException;
import com.paypay.constant.VariableConstant;
import com.paypay.dto.Request.InquiryAccountBankRequest;
import com.paypay.dto.Request.InquiryNpwpRequest;
import com.paypay.dto.Request.ReconPaymentStatusRequest;
import com.paypay.dto.Request.TransferRequest;
import com.paypay.dto.Response.Response;
import com.paypay.dto.Response.ResponseAccount;
import com.paypay.dto.Response.ResponseInquiryPayment;
import com.paypay.dto.Response.ResponseNpwp;
import com.paypay.model.UtilitiesAccount;
import com.paypay.model.UtilitiesNpwp;
import com.paypay.repository.UtilitiesAccountRepo;
import com.paypay.repository.UtilitiesNpwpRepo;

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

    @Autowired
    private RestTemplate restTemplate;

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
        // // TODO: handle exception
        // responseData.setMessage(e.getMessage());
        // responseData.setStatus(false);
        // return response = new Response(constantVar.getSTATUS_FAILED(), "Failed",
        // responseData);
        // }
        return response = new Response(constantVar.getSTATUS_OK(), "Success", responseNpwp);
    }

    @Transactional(rollbackOn = Exception.class)
    public Response inquiryDataAccountBank(InquiryAccountBankRequest request) throws Exception {
        ResponseAccount responseAccount = new ResponseAccount();
        UtilitiesAccount accountDb = null;
        // if ((request.getAccountType() != null && !request.getAccountType().isEmpty())
        // && (request.getBankName() != null && !request.getBankName().isEmpty())) {
        // accountDb =
        // utilitiesAccountRepo.findByAccountNumberAndBankNameAndCurrencyType(
        // request.getAccountNumber(),
        // request.getBankName(), request.getAccountType());
        // } else if (request.getAccountType() != null &&
        // !request.getAccountType().isEmpty()) {
        // accountDb =
        // utilitiesAccountRepo.findByAccountNumberAndCurrencyType(request.getAccountNumber(),
        // request.getAccountType());
        // } else if (request.getBankName() != null && !request.getBankName().isEmpty())
        // {
        // accountDb =
        // utilitiesAccountRepo.findByAccountNumberAndBankName(request.getAccountNumber(),
        // request.getBankName());
        // } else {
        // accountDb =
        // utilitiesAccountRepo.findByAccountNumber(request.getAccountNumber());
        // }

        accountDb = utilitiesAccountRepo.findByAccountNumberAndBankNameAndCurrencyType(
                request.getAccountNumber(),
                request.getBankName(), request.getAccountType());
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

    public Response transfer(TransferRequest request) throws Exception {
        // inquiry Taghian
        ResponseInquiryPayment tagihanPembayaran = null;
        try {
            tagihanPembayaran = inquiryTagihan(request.getVaNumber());
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println(e.getMessage());
            throw new BadRequestException("Inquiry Tagihan Gagal");
        }
        LocalDateTime today = LocalDateTime.now();
        Map<String, String> bankCode = new HashMap<>();
        bankCode.put("001", "BCA");
        bankCode.put("002", "BRI");
        bankCode.put("003", "Mandiri");

        String bankCodeVaNumber = request.getVaNumber().substring(4, 7);
        if(!bankCode.get(bankCodeVaNumber).equalsIgnoreCase(request.getBankName())){
            throw new BadRequestException("Virtual Account tidak tersedia");
        }
        tagihanPembayaran.setBankPayment(request.getBankName());
        tagihanPembayaran.setSourceAccount(request.getSourceAccount());
        // String bankCode = request.getVaNumber().substring(4, 7);
        UtilitiesAccount sourceAccountData = utilitiesAccountRepo.findByAccountNumber(request.getSourceAccount());
        if (request.getSourceAccount().equalsIgnoreCase(tagihanPembayaran.getDestinationAccount())) {
            throw new BadRequestException("Nomor Rekening ini tidak bisa di pakai untuk bayar");
        }
        if (!sourceAccountData.getCurrencyType().equalsIgnoreCase(tagihanPembayaran.getCurrency())) {
            throw new BadRequestException(
                    "Tipe mata uang rekening tidak sesuai dengan currency yang di transaksikan");
        }
        if (sourceAccountData.getAmount().compareTo(tagihanPembayaran.getTotalAmount()) < 0) {
            throw new BadRequestException("Saldo Rekening tidak cukup");
        }
        // proses debit dan kredit
        sourceAccountData.setAmount(sourceAccountData.getAmount().subtract(tagihanPembayaran.getTotalAmount()));
        sourceAccountData.setLastUpdate(today);
        UtilitiesAccount accountXchangeCredit = null;
        if (sourceAccountData.getBankName().equalsIgnoreCase("BCA")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("IDR")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("66603268781");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("BCA")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("USD")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("66604288745");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("BCA")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("SGD")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("66604222678");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("BCA")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("AUD")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("66604222670");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("BCA")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("EUR")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("66604222672");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("BRI")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("IDR")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("3330879552468");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("BRI")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("USD")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("3330879552462");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("BRI")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("SGD")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("3330879552461");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("BRI")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("AUD")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("3330879552465");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("BRI")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("EUR")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("3330879552464");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("Mandiri")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("IDR")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("777012544678521");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("Mandiri")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("USD")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("777012544678522");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("Mandiri")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("SGD")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("777012544678523");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("Mandiri")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("AUD")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("777012544678524");
        } else if (sourceAccountData.getBankName().equalsIgnoreCase("Mandiri")
                && sourceAccountData.getCurrencyType().equalsIgnoreCase("EUR")) {
            accountXchangeCredit = utilitiesAccountRepo.findByAccountNumber("777012544678525");
        }
        // credit ke nomor account xchange berdasarkan mappingan di atas
        accountXchangeCredit.setAmount(accountXchangeCredit.getAmount().add(tagihanPembayaran.getTotalAmount()));
        accountXchangeCredit.setLastUpdate(today);

        UtilitiesAccount accountXchangeDebit = null;
        UtilitiesAccount destinationAccount = utilitiesAccountRepo
                .findByAccountNumber(tagihanPembayaran.getDestinationAccount());
        if (destinationAccount.getBankName().equalsIgnoreCase("BCA")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("IDR")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("66603268781");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("BCA")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("USD")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("66604288745");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("BCA")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("SGD")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("66604222678");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("BCA")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("AUD")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("66604222670");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("BCA")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("EUR")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("66604222672");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("BRI")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("IDR")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("3330879552468");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("BRI")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("USD")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("3330879552462");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("BRI")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("SGD")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("3330879552461");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("BRI")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("AUD")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("3330879552465");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("BRI")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("EUR")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("3330879552464");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("Mandiri")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("IDR")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("777012544678521");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("Mandiri")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("USD")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("777012544678522");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("Mandiri")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("SGD")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("777012544678523");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("Mandiri")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("AUD")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("777012544678524");
        } else if (destinationAccount.getBankName().equalsIgnoreCase("Mandiri")
                && destinationAccount.getCurrencyType().equalsIgnoreCase("EUR")) {
            accountXchangeDebit = utilitiesAccountRepo.findByAccountNumber("777012544678525");
        }
        // debit to nomor rekening xchangeDebit
        accountXchangeDebit.setAmount(accountXchangeDebit.getAmount().subtract(tagihanPembayaran.getKreditAmount()));
        accountXchangeDebit.setLastUpdate(today);

        // credit to nomor rekening destination
        destinationAccount.setAmount(destinationAccount.getAmount().add(tagihanPembayaran.getKreditAmount()));
        destinationAccount.setLastUpdate(today);
        List<UtilitiesAccount> accountDebitCredit = new ArrayList<>();
        accountDebitCredit.add(sourceAccountData);
        accountDebitCredit.add(accountXchangeCredit);
        accountDebitCredit.add(accountXchangeDebit);
        accountDebitCredit.add(destinationAccount);
        utilitiesAccountRepo.saveAll(accountDebitCredit); 
        // Update Payment Status
        ReconPaymentStatusRequest requestReconPaymentStatus = new ReconPaymentStatusRequest();
        requestReconPaymentStatus.setIdPayment(tagihanPembayaran.getIdPayment());
        requestReconPaymentStatus.setPaymentStatus("3");
        requestReconPaymentStatus.setBankPayment(request.getBankName());
        requestReconPaymentStatus.setSourceAccount(request.getSourceAccount());
        reconPaymentStatus(requestReconPaymentStatus);
        // } catch (Exception e) {
        // // Update payment status gagal = 2
        // ReconPaymentStatusRequest requestReconPaymentStatus = new
        // ReconPaymentStatusRequest();
        // requestReconPaymentStatus.setIdPayment(tagihanPembayaran.getIdPayment());
        // requestReconPaymentStatus.setPaymentStatus("2");
        // reconPaymentStatus(requestReconPaymentStatus);
        // System.out.println(e.getMessage());
        // throw new BadRequestException("Transafer Gagal, " + e.getMessage());
        // }

        return response = new Response(constantVar.getSTATUS_OK(), "Success", "Transfer Success");
    }

    public ResponseInquiryPayment inquiryTagihan(String vaNumber) throws Exception {
        String url = "http://localhost:8484/payment/inquiry-tagihan/" + vaNumber;
        ResponseInquiryPayment responseInquiryTagihan = null;

        try {
            responseInquiryTagihan = restTemplate.getForEntity(url, ResponseInquiryPayment.class, 1).getBody();
            System.out.println("Response Inquiry Tagihan: " + responseInquiryTagihan);
            return responseInquiryTagihan;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error Inquiry Tagihan: " + e.getMessage());
            throw new BadRequestException("Inquiry Tagihan gagal");
        }

    }

    public Response reconPaymentStatus(ReconPaymentStatusRequest request) throws Exception {
        String url = "http://localhost:8484/payment/recon/payment-status";
        HttpEntity<Object> entity = new HttpEntity<Object>(request);
        try {
            response = restTemplate.exchange(url, HttpMethod.PUT, entity, Response.class, 1).getBody();
            System.out.println("Response Recon Payment Status " + response);
            return response;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("Error Inquiry Tagihan: " + e.getMessage());
            throw new BadRequestException("Inquiry Tagihan gagal");
        }

    }
}
