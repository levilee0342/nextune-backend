package com.example.nextune_backend.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "track_genre")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackGenre {
    @EmbeddedId
    TrackGenreId id;

    @ManyToOne
    @MapsId("genreId")
    @JoinColumn(name = "genre_id")
    Genre genre;

    @ManyToOne
    @MapsId("trackId")
    @JoinColumn(name="track_id")
    Track track;

}

