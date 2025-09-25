package com.example.nextune_backend.service;

import java.math.BigDecimal;
import java.util.Map;

public interface VNPayGatewayService {
    String createPaymentUrl(String orderInfo, String txnRef, BigDecimal amount, String clientIp);

    boolean verifyIPNSignature(Map<String, String> allParams);
}