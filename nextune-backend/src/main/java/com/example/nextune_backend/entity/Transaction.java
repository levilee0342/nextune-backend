package com.example.nextune_backend.entity;

import com.example.nextune_backend.entity.enums.PaymentMethod;
import com.example.nextune_backend.entity.enums.Status;
import com.example.nextune_backend.entity.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name= "transactions",
        indexes = {
                @Index(name="idx_tx_user", columnList = "user_id"),
                @Index(name="idx_tx_subscription", columnList = "subscription_id")
        })
@Data @NoArgsConstructor @AllArgsConstructor @Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {
    @EmbeddedId
    TransactionId id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("subscriptionId")
    @JoinColumn(name = "subscription_id")
    Subscription subscription;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name="user_id")
    User user;

    @Column(name= "paid_at")
    LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    PaymentMethod paymentMethod;

    @Column(name ="total_price", precision = 19, scale = 2, nullable = false)
    BigDecimal totalPrice;

    @Column(name ="total_tax", precision = 19, scale = 2, nullable = false)
    BigDecimal totalTax;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TransactionStatus status;

    // Lưu mã giao dịch từ cổng thanh toán (nếu có)
    @Column(name = "gateway_tx_id", length = 128)
    String gatewayTransactionId;

    // Idempotency key để chống double-charge
    @Column(name = "idempotency_key", length = 128, unique = true)
    String idempotencyKey;

    @Version
    Long version;
}
