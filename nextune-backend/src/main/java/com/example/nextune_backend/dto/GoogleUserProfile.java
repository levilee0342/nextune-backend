package com.example.nextune_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GoogleUserProfile {
    private final String sub;
    private final String email;
    private final Boolean email_verified;
    private final String name;
    private final String picture;
}
