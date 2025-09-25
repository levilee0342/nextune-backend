package com.example.nextune_backend.dto.response;

import com.example.nextune_backend.entity.enums.SubscriptionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubscriptionResponse(
        String id,
        String name,
        BigDecimal price,
        int durationInDays,
        SubscriptionStatus status,
        LocalDateTime releasedAt
){}