package com.example.nextune_backend.dto.request;

import lombok.Data;

@Data
public class TrackGenreRequest {
    private String trackId;
    private String genreId;
}
