package com.example.nextune_backend.service.impl;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

import org.springframework.stereotype.Service;

import com.example.nextune_backend.entity.RefreshToken;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.repository.RefreshTokenRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    
    private final RefreshTokenRepository repo;
    private final UserRepository userRepo;

    public RefreshToken store(String rawRefreshToken,
                              String email,
                              String ua,
                              String ip,
                              Instant exp,
                              String rawSessionToken) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found by email: " + email));

        // refresh token bắt buộc có
        String tokenHash = hash(Objects.requireNonNull(rawRefreshToken, "refresh token is null"));

        // session token (parent) có thể null ở lần cấp đầu
        String sessionHash = (rawSessionToken == null || rawSessionToken.isBlank())
                ? null
                : hash(rawSessionToken);

        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .sessionHash(sessionHash)
                .userAgent(ua == null ? "" : ua)
                .ip(ip == null ? "" : ip)
                .expiresAt(exp)
                .build();

        return repo.save(rt);
    }


    public Optional<RefreshToken> findActiveByRawToken(String raw) {
        return repo.findByTokenHashAndRevokedAtIsNull(hash(raw))
                   .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()));
    }

    public Optional<RefreshToken> findActiveByRawSessionToken(String rawSessionToken) {
        String sessionHash = hash(rawSessionToken);
        List<RefreshToken> tokens = repo.findBySessionHashAndRevokedAtIsNull(sessionHash);
        return tokens.stream()
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()))
                .max(Comparator.comparing(RefreshToken::getExpiresAt));
    }

    @Override
    @Transactional
    public void revoke(RefreshToken rt) {
        rt.setRevokedAt(Instant.now());
        repo.save(rt);
    }

    public void revokeSession(String rawSessionToken) {
        String sessionHash = hash(rawSessionToken); // hàm hash bạn đã có sẵn
        repo.revokeAllBySessionHash(sessionHash, Instant.now());
    }

    @Override
    @Transactional
    public void revokeAllByUserId(Long userId) {
        repo.revokeAllByUserId(userId, Instant.now());
    }

    public String hash(String raw) {
        try {
            return Base64.getEncoder().encodeToString(
                    MessageDigest.getInstance("SHA-256")
                            .digest(raw.getBytes(StandardCharsets.UTF_8))
            );
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
    @Override
    @Transactional
    public void revokeBySessionToken(String rawSessionToken) {
        if (rawSessionToken == null || rawSessionToken.isBlank()) return;
        String sessionHash = hash(rawSessionToken);
        repo.revokeAllBySessionHash(sessionHash, Instant.now()); // revoke tất cả token thuộc phiên này
    }

}
