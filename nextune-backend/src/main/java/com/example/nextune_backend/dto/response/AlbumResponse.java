package com.example.nextune_backend.dto.response;

import com.example.nextune_backend.entity.EntityType;
import com.example.nextune_backend.entity.Status;
import com.example.nextune_backend.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AlbumResponse {
    String id;
    String name;
    LocalDate releaseDate;
    String imgUrl;
    String artistId;
    Integer totalDuration;
    Integer totalSong;
    Integer totalSaves;
    String entityType;
    String status;
}
