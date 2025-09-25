package com.example.nextune_backend.entity;

import com.example.nextune_backend.entity.enums.AuthProvider;
import com.example.nextune_backend.entity.enums.Gender;
import com.example.nextune_backend.entity.enums.Status;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    String description;
    String email;
    String password;
    String avatar;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(name = "date_of_birth")
    LocalDateTime dateOfBirth;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "is_premium")
    Boolean isPremium;

    @Enumerated(EnumType.STRING)
    Status status = Status.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "premium_due_date")
    LocalDateTime premiumDueDate;

    @Column(name = "violate_count")
    Integer violateCount;

    @Column(name = "google_sub", unique = true)
    String googleSub;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    AuthProvider provider = AuthProvider.LOCAL;
}
