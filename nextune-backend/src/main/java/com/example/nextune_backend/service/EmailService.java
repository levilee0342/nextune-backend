package com.example.nextune_backend.service;

import java.util.List;

public interface EmailService {
    void sendOtp(String recipientEmail, String otpCode);
    void sendEmail(String to, String subject, String content);
    void sendEmailMultiple(List<String> toList, String subject, String content);
}