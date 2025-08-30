package com.example.nextune_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OtpVerifyRequest(
        @NotBlank @Email String email,
        @NotBlank String code,
        @NotNull OtpPurpose purpose
){}