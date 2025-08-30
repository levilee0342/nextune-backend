package com.example.nextune_backend.entity;

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

    @Enumerated
    Gender gender;

    @Column(name = "date_of_birth")
    LocalDateTime dateOfBirth;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "is_premium")
    Boolean isPremium;

    @Enumerated
    private Status status = Status.ACTIVE;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "premium_due_date")
    LocalDateTime premiumDueDate;

    @Column(name = "violate_count")
    Integer violateCount;

}
