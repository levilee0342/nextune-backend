package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "track_collection")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class TrackCollection {
    @EmbeddedId
    TrackCollectionId id;

    @ManyToOne
    @MapsId("trackId")
    @JoinColumn(name= "track_id")
    Track track;

    @ManyToOne
    @MapsId("playlistId")
    @JoinColumn(name="playlist_id")
    Playlist playlist;

    @Column(name = "added_at")
    LocalDateTime addedAt;

    @Column(name = "track_order")
    Integer trackOrder;
}
