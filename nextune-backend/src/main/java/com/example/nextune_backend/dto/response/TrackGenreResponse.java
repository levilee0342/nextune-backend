package com.example.nextune_backend.dto.response;

import lombok.Data;

@Data
public class TrackGenreResponse {
    private String trackId;
    private String trackName;
    private String genreId;
    private String genreName;
}
