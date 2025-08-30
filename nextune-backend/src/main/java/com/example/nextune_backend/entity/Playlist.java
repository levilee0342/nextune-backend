package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "playlist")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;

    String description;

    @Column(name = "img_url")
    String imgUrl;

    @Column(name = "is_public")
    Boolean isPublic;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "total_duration")
    Integer totalDuration;

    @Column(name = "total_tracks")
    Integer totalTracks;

    @Column(name = "total_followers")
    Integer totalFollowers;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Enumerated
    Status status = Status.UNPUBLISHED;
}
