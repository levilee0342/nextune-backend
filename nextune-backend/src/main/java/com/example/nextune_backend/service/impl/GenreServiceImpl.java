package com.example.nextune_backend.service.impl;

import java.util.List;

import com.example.nextune_backend.dto.response.GenreResponse;
import com.example.nextune_backend.mapper.GenreMapper;
import org.springframework.stereotype.Service;

import com.example.nextune_backend.dto.request.GenreRequest;
import com.example.nextune_backend.entity.Genre;
import com.example.nextune_backend.repository.GenreRepository;
import com.example.nextune_backend.service.GenreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    public GenreResponse createGenre(GenreRequest request) {
        Genre genre = genreMapper.toEntity(request);
        return genreMapper.toResponse(genreRepository.save(genre));
    }

    @Override
    public List<GenreResponse> getAllGenres() {
        return genreMapper.toResponseList(genreRepository.findAll());
    }

    @Override
    public GenreResponse getGenreById(String id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        return genreMapper.toResponse(genre);
    }

    @Override
    public GenreResponse updateGenreById(String id, GenreRequest request) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Genre not found"));
        genreMapper.updateGenreFromRequest(request, genre);
        return genreMapper.toResponse(genreRepository.save(genre));
    }

    @Override
    public void deleteGenre(String id) {
        genreRepository.findById(id)
                .ifPresentOrElse(
                        genreRepository::delete,
                        () -> {
                            throw new RuntimeException("Genre not found: " + id);
                        });
    }
}
