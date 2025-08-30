package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.AlbumGenreRequest;
import com.example.nextune_backend.dto.response.AlbumGenreResponse;

import java.util.List;

public interface AlbumGenreService {
    AlbumGenreResponse addGenreToAlbum(AlbumGenreRequest request);
    void removeGenreFromAlbum(String albumId, String genreId);
    List<AlbumGenreResponse> getGenresByAlbum(String albumId);
}
