package com.example.nextune_backend.dto.request;

import java.time.LocalDateTime;

import com.example.nextune_backend.entity.enums.EntityType;
import com.example.nextune_backend.entity.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class TrackRequest {
    private String id;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishedAt;
    private Integer duration;
    private Long playCount;
    private String lyric;
    private String imgUrl;
    private String trackUrl;
    private Boolean explicit;
    private EntityType entityType;
    private String albumId;  
    private Status status;
    private Boolean isPlaying = false;
    private Integer trackOrder;
    private String color;
    private String description;
}
