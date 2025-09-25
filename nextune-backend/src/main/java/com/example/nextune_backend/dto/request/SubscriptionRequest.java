package com.example.nextune_backend.dto.request;

import com.example.nextune_backend.entity.enums.SubscriptionStatus;

import java.math.BigDecimal;

public record SubscriptionRequest(
        String name,
        BigDecimal price,
        int durationInDays,
        SubscriptionStatus status
){}