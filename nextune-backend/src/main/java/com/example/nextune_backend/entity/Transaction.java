package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name= "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {
    @EmbeddedId
    TransactionId id;

    @ManyToOne
    @MapsId("subcriptionId")
    @JoinColumn(name = "subcription_id")
    private Subcription subcription;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name="user_id")
    private User user;

    @Column(name= "paid_at")
    LocalDateTime paidAt;

    @Column(name = "payment_method")
    String paymentMethod;

    @Column(name ="total_price")
    BigDecimal totalPrice;

    @Column(name ="total_tax")
    BigDecimal totalTax;

    @Enumerated
    Status status;

}
