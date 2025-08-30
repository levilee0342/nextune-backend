package com.example.nextune_backend.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "user_genre")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserGenre {
    @EmbeddedId
    UserGenreId id;

    @ManyToOne
    @MapsId("genreId")
    @JoinColumn(name = "genre_id")
    Genre genre;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name="user_id")
    User user;

}

