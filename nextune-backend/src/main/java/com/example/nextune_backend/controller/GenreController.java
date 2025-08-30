package com.example.nextune_backend.controller;

import java.util.List;

import com.example.nextune_backend.dto.response.GenreResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.nextune_backend.dto.request.GenreRequest;
import com.example.nextune_backend.dto.response.ApiResponse;
import com.example.nextune_backend.entity.Genre;
import com.example.nextune_backend.service.GenreService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @PostMapping()
    public ResponseEntity<GenreResponse> createGenre(@RequestBody GenreRequest request) {
        GenreResponse genre = genreService.createGenre(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(genre);
    }

    @GetMapping("/all")
    public ResponseEntity<List<GenreResponse>> getAllGenres() {
        List<GenreResponse> genres = genreService.getAllGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreResponse> getGenreById(@PathVariable String id) {
        GenreResponse genre = genreService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreResponse> updateGenre(
            @PathVariable String id, @RequestBody GenreRequest request) {
        GenreResponse genre = genreService.updateGenreById(id, request);
        return ResponseEntity.ok(genre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable String id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
