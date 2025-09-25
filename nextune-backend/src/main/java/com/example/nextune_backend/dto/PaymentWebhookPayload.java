package com.example.nextune_backend.dto;

import java.math.BigDecimal;

public record PaymentWebhookPayload(
        String gatewayTransactionId,
        String status, // "PAID", "FAILED", "REFUNDED"...
        BigDecimal paidAmount,
        String signature
){}
