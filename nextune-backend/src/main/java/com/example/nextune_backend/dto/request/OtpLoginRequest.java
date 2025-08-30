package com.example.nextune_backend.dto.request;

import lombok.Data;

@Data
public class OtpLoginRequest {
    private String email;
    private String otp;
    private String roleId;
}
