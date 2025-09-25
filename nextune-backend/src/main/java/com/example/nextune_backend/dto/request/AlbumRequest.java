package com.example.nextune_backend.dto.request;

import java.time.LocalDate;

import com.example.nextune_backend.entity.enums.EntityType;
import com.example.nextune_backend.entity.enums.Status;

import lombok.Data;

@Data
public class AlbumRequest {

    private String id;
    private String name;
    private LocalDate releaseDate;
    private String imgUrl;
    private String artistId;
    private Integer totalDuration;
    private Integer totalSong;
    private Integer totalSaves;
    private EntityType entityType;
    private Status status;
    private String color;
}
