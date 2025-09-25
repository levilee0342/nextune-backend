package com.example.nextune_backend.entity;

import com.example.nextune_backend.entity.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

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

    @Enumerated(EnumType.STRING)
    Status status = Status.FOLLOWED;
}

