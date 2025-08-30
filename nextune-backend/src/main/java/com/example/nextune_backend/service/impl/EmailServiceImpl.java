package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}") private String sender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOtp(String recipientEmail, String otpCode) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(sender);
        msg.setTo(recipientEmail);
        msg.setSubject("[Nextune] Your OTP Code");
        msg.setText("""
                Your OTP code is: %s
                It expires in 5 minutes.
                If you did not request this, please ignore this email.
                """.formatted(otpCode));
        mailSender.send(msg);
    }
}