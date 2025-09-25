package com.example.nextune_backend.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import com.example.nextune_backend.entity.enums.Status;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AlbumResponse {

    private String id;
    private String name;
    private LocalDate releaseDate;
    private String imgUrl;
    private Integer totalDuration;
    private Integer totalSong;
    private Integer totalSaves;
    private String entityType;
    private Status status;
    private String artistId;
    private String artistName;
    private String color;
}
