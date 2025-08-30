package com.example.nextune_backend.dto.request;

import lombok.Data;

@Data
public class AlbumGenreRequest {
    private String albumId;
    private String genreId;
}
