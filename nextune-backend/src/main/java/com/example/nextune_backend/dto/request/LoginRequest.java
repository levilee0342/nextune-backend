package com.example.nextune_backend.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
    private String roleId;
}
