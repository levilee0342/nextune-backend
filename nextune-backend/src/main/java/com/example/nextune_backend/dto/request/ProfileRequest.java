package com.example.nextune_backend.dto.request;

import java.time.LocalDateTime;

import com.example.nextune_backend.entity.Gender;

import lombok.Data;

@Data
public class ProfileRequest {
    private String name;
    private Gender gender;
    private LocalDateTime dateOfBirth;
    private String avatar;
}
