package com.example.nextune_backend.dto.response;

public record PurchaseInitResponse(
        String subscriptionId,
        String userId,
        String redirectUrl,
        String gatewayTransactionId,
        String idempotencyKey
){}
