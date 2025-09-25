package com.example.nextune_backend.dto.request;

import java.time.LocalDateTime;

import com.example.nextune_backend.entity.Role;

import com.example.nextune_backend.entity.enums.Gender;
import com.example.nextune_backend.entity.enums.Status;

import lombok.Data;

@Data
public class ProfileRequest {

    private String id;
    private String email;
    private String name;
    private String avatar;
    private Gender gender;
    private String description;
    private Role role;
    private LocalDateTime dateOfBirth;
    private Boolean isPremium;
    private LocalDateTime createdAt;
    private Status status;
    private LocalDateTime premiumDueDate;
    private Integer violateCount;
}
