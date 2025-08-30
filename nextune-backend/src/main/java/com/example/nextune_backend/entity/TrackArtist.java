package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "TrackArtist")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrackArtist {
    @EmbeddedId
    TrackArtistId id;

    @ManyToOne
    @MapsId("trackId")
    @JoinColumn(name = "track_id")
    Track track;

    @ManyToOne
    @MapsId("artistId")
    @JoinColumn(name = "artist_id")
    User artist;

    @Column(name = "role_in_track")
    String roleInTrack;
}


