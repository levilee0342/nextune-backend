package com.example.nextune_backend.service.impl;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.nextune_backend.entity.RefreshToken;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.repository.RefreshTokenRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    
    private final RefreshTokenRepository repo;
    private final UserRepository userRepo;

    public RefreshToken store(String rawRefreshToken, String email, String ua, String ip, Instant exp, String rawSessionToken) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String hash = hash(rawRefreshToken);
        String ssHash = hash(rawSessionToken);
        RefreshToken rt = RefreshToken.builder()
                .user(user).tokenHash(hash).expiresAt(exp)
                .userAgent(ua).ip(ip).sessionHash(ssHash)
                .build();
        return repo.save(rt);
    }

    public Optional<RefreshToken> findActiveByRawToken(String raw) {
        return repo.findByTokenHashAndRevokedAtIsNull(hash(raw))
                   .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()));
    }

    public Optional<RefreshToken> findActiveByRawSessionToken(String rawSessionToken) {
    return repo.findBySessionHashAndRevokedAtIsNull(hash(rawSessionToken))
               .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()));
    }



    public void revoke(RefreshToken rt) {
        rt.setRevokedAt(Instant.now());
        repo.save(rt);
    }

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

}
