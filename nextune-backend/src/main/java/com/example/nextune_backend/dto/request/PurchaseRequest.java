package com.example.nextune_backend.dto.request;

import com.example.nextune_backend.entity.enums.PaymentMethod;

public record PurchaseRequest(
        String subscriptionId,
        PaymentMethod paymentMethod,
        String idempotencyKey // FE sinh UUID, gửi mỗi lần mua
){}