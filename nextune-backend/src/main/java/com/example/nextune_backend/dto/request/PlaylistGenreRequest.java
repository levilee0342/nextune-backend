package com.example.nextune_backend.dto.request;

import lombok.Data;

@Data
public class PlaylistGenreRequest {
    private String playlistId;
    private String genreId;
}
