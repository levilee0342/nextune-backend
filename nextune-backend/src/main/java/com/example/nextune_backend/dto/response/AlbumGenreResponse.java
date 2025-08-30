package com.example.nextune_backend.dto.response;

import lombok.Data;

@Data
public class AlbumGenreResponse {
    private String albumId;
    private String albumName;
    private String genreId;
    private String genreName;
}
