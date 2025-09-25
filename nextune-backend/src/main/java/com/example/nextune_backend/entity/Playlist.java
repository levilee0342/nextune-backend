package com.example.nextune_backend.entity;

import com.example.nextune_backend.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Formula;

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

    String color;

    Boolean isProfile = true;

    @Column(name = "img_url")
    String imgUrl;

    @Column(name = "is_public")
    Boolean isPublic;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    LocalDateTime deletedAt;

    @Column(name = "total_duration")
    Integer totalDuration;

    @Column(name = "total_tracks")
    Integer totalTracks;

    @Column(name = "total_followers")
    Integer totalFollowers;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @Enumerated(EnumType.STRING)
    Status status = Status.UNPUBLISHED;

    @Formula("(SELECT COALESCE(SUM(t.play_count),0) " +
            " FROM track_collection tc " +
            " JOIN track t ON t.id = tc.track_id " +
            " WHERE tc.playlist_id = id AND t.status = 'PUBLISHED')")
    Long totalListenCount;
}
