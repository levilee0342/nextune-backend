package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.OtpRequest;
import com.example.nextune_backend.dto.request.OtpVerifyRequest;
import com.example.nextune_backend.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/request")
    public ResponseEntity<Void> request(@Valid @RequestBody OtpRequest req) {
        otpService.requestOtp(req.email(), req.purpose());
        return ResponseEntity.noContent().build(); // 204
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@Valid @RequestBody OtpVerifyRequest req) {
        otpService.verifyAndConsume(req.email(), req.code(), req.purpose());
        return ResponseEntity.noContent().build(); // 204 nếu OK
    }
}
