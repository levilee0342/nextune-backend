package com.example.nextune_backend.repository;

import com.example.nextune_backend.dto.request.OtpPurpose;
import com.example.nextune_backend.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findByEmailAndPurpose(String email, OtpPurpose purpose);
    Optional<OtpToken> findByEmail(String email);
    void deleteByEmailAndPurpose(String email, OtpPurpose purpose);

}