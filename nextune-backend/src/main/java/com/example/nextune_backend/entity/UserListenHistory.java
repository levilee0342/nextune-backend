package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_listen_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserListenHistory {
    @EmbeddedId
    UserListenHistoryId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;

    @ManyToOne
    @MapsId("trackId")
    @JoinColumn(name = "track_id", insertable = false, updatable = false)
    Track track;

    @Column(name ="role_in_track")
    RoleName roleInTrack;

    @Column(name = "skipped_at")
    LocalDateTime skippedAt;
}


