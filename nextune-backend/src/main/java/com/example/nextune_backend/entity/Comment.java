package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
@Table(name = "comment")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Comment {
    @EmbeddedId
    CommentId id;

    @ManyToOne
    @MapsId("trackId")
    @JoinColumn(name ="track_id")
    Track track;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name="user_id")
    User user;

    @Column(name="comment_date")
    LocalDateTime commentDate;

    String content;
}
