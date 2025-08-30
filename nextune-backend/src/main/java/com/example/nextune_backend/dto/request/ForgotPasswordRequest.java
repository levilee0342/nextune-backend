package com.example.nextune_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String newPassword,
        @NotBlank String confirmPassword
) {}
