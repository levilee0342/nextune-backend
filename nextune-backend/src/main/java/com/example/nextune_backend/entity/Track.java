package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "track")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;

    @Column(name = "published_at")
    LocalDateTime publishedAt;

    Integer duration;

    @Column(name = "play_count")
    Long playCount;

    String lyric;

    @Column(name = "img_url")
    String imgUrl;

    @Column(name = "track_url")
    String trackUrl;

    Boolean explicit;

    @Column(name = "entity_type")
    String entityType;

    @ManyToOne
    @JoinColumn(name = "album_id")
    Album album;

    @Enumerated
    Status status = Status.PUBLISHED;


}

