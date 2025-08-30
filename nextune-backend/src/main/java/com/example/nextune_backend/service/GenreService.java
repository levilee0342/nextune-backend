package com.example.nextune_backend.service;

import java.util.List;

import com.example.nextune_backend.dto.request.GenreRequest;
import com.example.nextune_backend.dto.response.GenreResponse;
import com.example.nextune_backend.entity.Genre;

public interface GenreService {
    GenreResponse createGenre(GenreRequest request);

    List<GenreResponse> getAllGenres();

    GenreResponse getGenreById(String id);

    GenreResponse updateGenreById(String id, GenreRequest request);

    void deleteGenre(String id);
}
