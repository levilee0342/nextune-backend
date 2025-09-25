package com.example.nextune_backend.entity;

import com.example.nextune_backend.entity.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name ="subscriptions")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true, length = 100)
    String name;

    @Column(name="price", precision = 19, scale = 2, nullable = false)
    BigDecimal price;

    @Column(name="duration_in_days", nullable = false)
    int durationInDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(name ="released_at", nullable = false)
    LocalDateTime releasedAt;
}

