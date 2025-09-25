package com.example.nextune_backend.entity;

import com.example.nextune_backend.entity.enums.EntityType;
import com.example.nextune_backend.entity.enums.Status;
import jakarta.annotation.Nullable;
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
    Long playCount = 0L;

    String lyric;

    @Column(name = "img_url")
    String imgUrl;

    @Column(name = "track_url")
    String trackUrl;

    Boolean explicit;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    EntityType entityType;

    @ManyToOne
    @JoinColumn(name = "album_id")
    @Nullable
    Album album;

    @Enumerated(EnumType.STRING)
    Status status = Status.PUBLISHED;

    Boolean isPlaying = false;

    @Column(name = "track_order")
    Integer trackOrder;

    String color;

    String description;
}

