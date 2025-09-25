package com.example.nextune_backend.dto.response;

import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.enums.EntityType;
import com.example.nextune_backend.entity.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackResponse {
    private String id;
    private String name;
    private Integer duration;
    private String imgUrl;
    private String trackUrl;
    private EntityType entityType;
    private Status status;
    private String albumId;
    private String albumName;
    private String artistId;
    private String artistName;
    private String lyric;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime publishedAt;
    private Long playCount;
    private Boolean explicit;
    private Boolean isPlaying;
    private Integer trackOrder;
    private String color;
    private String description;

}