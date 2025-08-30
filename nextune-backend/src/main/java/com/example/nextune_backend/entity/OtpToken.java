package com.example.nextune_backend.entity;

import com.example.nextune_backend.dto.request.OtpPurpose;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_tokens", indexes = {
        @Index(name = "idx_otp_email_purpose", columnList = "email,purpose", unique = true)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class OtpToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable=false, length=255)
    String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=32)
    OtpPurpose purpose;

    @Column(nullable=false, length=120)
    String codeHash; // hash OTP bằng BCrypt

    @Column(nullable=false)
    LocalDateTime expiresAt;

    @Column(nullable=false)
    LocalDateTime createdAt;

    @Column(nullable=false)
    int attempts; // số lần verify đã thử

    @Column
    LocalDateTime lastSentAt; // chặn spam resend


}