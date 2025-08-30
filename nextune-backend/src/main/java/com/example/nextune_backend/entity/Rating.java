package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "rating")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Rating {
    @EmbeddedId
    RatingId id;

    @ManyToOne
    @MapsId("podcastCollectionId")
    @JoinColumn(name = "album_id")
    Album album;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @Column(name ="number_of_star")
    Integer numberOfStar;
}
