package com.example.nextune_backend.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "album_genre")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AlbumGenre {
    @EmbeddedId
    AlbumGenreId id;

    @ManyToOne
    @MapsId("genreId")
    @JoinColumn(name = "genre_id")
    Genre genre;

    @ManyToOne
    @MapsId("albumId")
    @JoinColumn(name="album_id")
    Album album;

}

