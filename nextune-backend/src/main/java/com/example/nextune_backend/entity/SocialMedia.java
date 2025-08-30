package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name="social_medias")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SocialMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    User user;

    @Column(name = "facebook_url")
    String facebookUrl;

    @Column(name ="instagram_url")
    String instagramUrl;

    @Column(name ="x_url")
    String xUrl;
}
