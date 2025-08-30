package com.example.nextune_backend.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


public record OtpRequest(
        @NotBlank @Email String email,
        @NotNull OtpPurpose purpose
){
    public String getEmail() {
        return email;
    }
}