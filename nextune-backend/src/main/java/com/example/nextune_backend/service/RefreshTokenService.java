package com.example.nextune_backend.service;



import com.example.nextune_backend.entity.RefreshToken;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken store(String rawRefreshToken, String username, String ua, String ip, Instant exp, String rawSessionToken);
    Optional<RefreshToken> findActiveByRawToken(String raw);
    Optional<RefreshToken> findActiveByRawSessionToken(String raw);
    void revoke(RefreshToken rt);
    void revokeAllByUserId(Long userId);
    String hash(String raw);
}
