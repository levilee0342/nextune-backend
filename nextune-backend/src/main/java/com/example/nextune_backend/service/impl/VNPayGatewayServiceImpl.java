package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.VNPayProperties;
import com.example.nextune_backend.service.VNPayGatewayService;
import com.example.nextune_backend.utility.VNPayUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class VNPayGatewayServiceImpl implements VNPayGatewayService {
    private final VNPayProperties props;


    @Override
    public String createPaymentUrl(String orderInfo, String txnRef, BigDecimal amount, String clientIp) {
// VNPay expects amount in VND * 100
        long amountVnd = amount.longValue();
        String amountStr = String.valueOf(amountVnd * 100);


        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "pay");
        vnpParams.put("vnp_TmnCode", props.getTmnCode());
        vnpParams.put("vnp_Amount", amountStr);
        vnpParams.put("vnp_CurrCode", props.getCurrCode());
        vnpParams.put("vnp_TxnRef", txnRef);
        vnpParams.put("vnp_OrderInfo", orderInfo);
        vnpParams.put("vnp_OrderType", "other");
        vnpParams.put("vnp_Locale", props.getLocale());
        vnpParams.put("vnp_ReturnUrl", props.getReturnUrl());
        vnpParams.put("vnp_IpAddr", (clientIp == null || clientIp.isBlank()) ? "127.0.0.1" : clientIp);


        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        ZonedDateTime nowVn = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        vnpParams.put("vnp_CreateDate", nowVn.format(fmt));
        vnpParams.put("vnp_ExpireDate", nowVn.plusMinutes(props.getExpireMinutes()).format(fmt));


// Build hash data
        String queryNoHash = VNPayUtility.buildQuery(vnpParams);
        String secureHash = VNPayUtility.hmacSHA512(props.getHashSecret(), queryNoHash);


        return props.getPayUrl() + "?" + queryNoHash + "&vnp_SecureHash=" + secureHash;
    }


    @Override
    public boolean verifyIPNSignature(Map<String, String> allParams) {
// Copy and remove vnp_SecureHash for hashing
        Map<String, String> sorted = new TreeMap<>(allParams);
        String receivedHash = sorted.remove("vnp_SecureHash");
        if (receivedHash == null) return false;
// VNPay also sometimes sends vnp_SecureHashType; remove it
        sorted.remove("vnp_SecureHashType");


        String toHash = VNPayUtility.buildQuery(sorted);
        String expected = VNPayUtility.hmacSHA512(props.getHashSecret(), toHash);
        return expected.equalsIgnoreCase(receivedHash);
    }
}