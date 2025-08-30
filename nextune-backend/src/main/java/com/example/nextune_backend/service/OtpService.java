package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.OtpPurpose;

public interface OtpService {
    void requestOtp(String email, OtpPurpose purpose);      // gửi OTP (201/204)
    boolean verifyAndConsume(String email, String code, OtpPurpose purpose); // true nếu OK
}