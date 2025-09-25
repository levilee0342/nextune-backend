package com.example.nextune_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Embeddable
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionId implements Serializable {
    @Column(name = "subscription_id", nullable = false)
    String subscriptionId;

    @Column(name = "user_id", nullable = false)
    String userId;
}
