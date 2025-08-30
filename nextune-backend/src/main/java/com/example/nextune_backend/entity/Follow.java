package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Table(name = "follow")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Follow {
    @EmbeddedId
    FollowId id;

    @ManyToOne
    @MapsId("followingId")
    @JoinColumn(name = "following_id")
    User followingUser;

    @ManyToOne
    @MapsId("followerId")
    @JoinColumn(name="follower_id")
    User followerUser;

    @Enumerated
    Status status = Status.FOLLOWED;
}

