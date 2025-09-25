package com.example.nextune_backend.repository;


import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.nextune_backend.entity.RefreshToken;
import java.util.List;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHashAndRevokedAtIsNull(String tokenHash);
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE RefreshToken rt
           SET rt.revokedAt = :now
         WHERE rt.sessionHash = :sessionHash
           AND rt.revokedAt IS NULL
    """)
    int revokeAllBySessionHash(@Param("sessionHash") String sessionHash, @Param("now") Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE RefreshToken rt
           SET rt.revokedAt = :now
         WHERE rt.user.id = :userId
           AND rt.revokedAt IS NULL
    """)
    int revokeAllByUserId(@Param("userId") Long userId, @Param("now") Instant now);

    // Nếu bạn có finder để refresh theo session:
    List<RefreshToken> findBySessionHashAndRevokedAtIsNull(String sessionHash);


}
