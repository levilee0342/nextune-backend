package com.example.nextune_backend.dto.response;

import java.time.LocalDateTime;

import com.example.nextune_backend.entity.enums.Gender;
import com.example.nextune_backend.entity.Role;
import com.example.nextune_backend.entity.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {
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
