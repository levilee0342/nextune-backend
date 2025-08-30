package com.example.nextune_backend.service;

public interface EmailService {
    void sendOtp(String recipientEmail, String otpCode);
}