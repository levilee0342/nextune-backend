package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "album_playlist_collection")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AlbumPlaylistCollection {
    @EmbeddedId
    AlbumPlaylistCollectionId id;

    @ManyToOne
    @MapsId("playlistId")
    @JoinColumn(name = "playlist_id")
    Playlist playlist;

    @ManyToOne
    @MapsId("albumId")
    @JoinColumn(name = "album_id")
    Album album;

    @Column(name = "added_at")
    LocalDateTime addedAt;

    @Column(name = "track_order")
    Integer trackOrder;
}
