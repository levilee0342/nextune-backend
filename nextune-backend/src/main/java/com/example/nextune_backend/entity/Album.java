package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "albums")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;

    @Column(name = "release_date")
    LocalDate releaseDate;

    @Column(name = "img_url")
    String imgUrl;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    User artist;

    @Column(name = "total_duration")
    Integer totalDuration;

    @Column(name = "total_song")
    Integer totalSong;

    @Column(name = "total_saves")
    Integer totalSaves;

    @Enumerated
    EntityType entityType;

    @Enumerated
    Status status = Status.PUBLISHED;
}
