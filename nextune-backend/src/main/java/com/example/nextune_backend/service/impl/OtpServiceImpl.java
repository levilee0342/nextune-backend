package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.OtpPurpose;
import com.example.nextune_backend.entity.OtpToken;
import com.example.nextune_backend.repository.OtpTokenRepository;
import com.example.nextune_backend.service.EmailService;
import com.example.nextune_backend.service.OtpService;
import com.example.nextune_backend.utility.PasswordEncoderUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final Duration TTL = Duration.ofMinutes(5);
    private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
    private static final int MAX_ATTEMPTS = 5;

    private final OtpTokenRepository otpRepo;
    private final EmailService emailService;


    private String generateOtp() {
        SecureRandom rnd = new SecureRandom();
        int n = rnd.nextInt(900_000) + 100_000; // 6 digits
        return String.valueOf(n);
    }

    @Override
    @Transactional
    public void requestOtp(String email, OtpPurpose purpose) {
        LocalDateTime now = LocalDateTime.now();
        OtpToken token = otpRepo.findByEmailAndPurpose(email, purpose).orElse(null);

        if (token != null && token.getLastSentAt() != null &&
                Duration.between(token.getLastSentAt(), now).compareTo(RESEND_COOLDOWN) < 0) {
            long wait = RESEND_COOLDOWN.minus(Duration.between(token.getLastSentAt(), now)).toSeconds();
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Please wait " + wait + " seconds before requesting a new OTP");
        }

        String code = generateOtp();
        String hash = PasswordEncoderUtility.encodePassword(code);

        if (token == null) {
            token = new OtpToken();
            token.setEmail(email);
            token.setPurpose(purpose);
            token.setCreatedAt(now);
            token.setAttempts(0);
        }
        token.setCodeHash(hash);
        token.setExpiresAt(now.plus(TTL));
        token.setLastSentAt(now);

        otpRepo.save(token);

        // gửi email
        emailService.sendOtp(email, code);
    }

    @Override
    @Transactional
    public boolean verifyAndConsume(String email, String code, OtpPurpose purpose) {
        OtpToken token = otpRepo.findByEmailAndPurpose(email, purpose)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP not found"));

        if (LocalDateTime.now().isAfter(token.getExpiresAt())) {
            otpRepo.delete(token);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP expired");
        }

        if (token.getAttempts() >= MAX_ATTEMPTS) {
            otpRepo.delete(token);
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many attempts");
        }

        token.setAttempts(token.getAttempts() + 1);

        boolean ok = PasswordEncoderUtility.matches(code, token.getCodeHash());
        if (!ok) {
            otpRepo.save(token);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid OTP");
        }

        // consume: xoá sau khi dùng
        otpRepo.delete(token);
        return true;
    }
}