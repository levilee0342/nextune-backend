package com.example.nextune_backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TrackCollectionResponse {
    private String trackId;
    private String playlistId;
    private Integer trackOrder;
    private LocalDateTime addedAt;
}