package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.*;
import com.example.nextune_backend.dto.response.LoginResponse;
import com.example.nextune_backend.dto.response.RegisterResponse;
import com.example.nextune_backend.dto.response.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest reques, HttpServletRequest httpServletRequest);

    LoginResponse loginWithOtp(OtpLoginRequest request, HttpServletRequest httpServletRequest);

    void requestPasswordResetOtp(ForgotPasswordRequest request);

    void resetPasswordWithOtp(ResetPasswordRequest request);

    void logout(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<?> refreshSession(HttpServletRequest request);

    ResponseEntity<UserInfoResponse> checkAuth(HttpServletRequest request);
}
