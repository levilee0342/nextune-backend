package com.example.nextune_backend.dto.request;
import com.example.nextune_backend.entity.EntityType;
import com.example.nextune_backend.entity.Status;
import lombok.Data;


import java.time.LocalDate;

@Data
public class AlbumRequest {
    private String name;
    private LocalDate releaseDate;
    private String imgUrl;
    private String artistId;
    private Integer totalDuration;
    private Integer totalSong;
    private Integer totalSaves;
    private EntityType entityType;
    private Status status;
}


