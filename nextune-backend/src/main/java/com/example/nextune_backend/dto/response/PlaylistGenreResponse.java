package com.example.nextune_backend.dto.response;

import lombok.Data;

@Data
public class PlaylistGenreResponse {
    private String playlistId;
    private String playlistName;
    private String genreId;
    private String genreName;
}

