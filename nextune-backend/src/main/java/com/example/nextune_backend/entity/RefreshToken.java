package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens", indexes = @Index(columnList = "user_id"))
@Data 
@Builder 
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 200)
    private String tokenHash;              // only save hash, NOT save raw token

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant revokedAt;
    private String userAgent;
    private String ip;

    @Column(nullable = false, unique = true, length = 200)
    private String sessionHash;
}

