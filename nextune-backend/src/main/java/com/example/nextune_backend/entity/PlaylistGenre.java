package com.example.nextune_backend.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "playlist_genre")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlaylistGenre {
    @EmbeddedId
    PlaylistGenreId id;

    @ManyToOne
    @MapsId("genreId")
    @JoinColumn(name = "genre_id")
    Genre genre;

    @ManyToOne
    @MapsId("playlistId")
    @JoinColumn(name="playlist_id")
    Playlist playlist;

}

