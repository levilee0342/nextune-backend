package com.example.nextune_backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrackCollectionResponse {
    private String trackId;
    private String playlistId;
    private Integer trackOrder;
    private LocalDateTime addedAt;
    private String trackName;
    private String trackImgUrl;
    private Integer trackDuration;
    private LocalDateTime publishedAt;
    private String artistId;
    private String artistName;
}