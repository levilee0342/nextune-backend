package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.AlbumGenreRequest;
import com.example.nextune_backend.dto.response.AlbumGenreResponse;
import com.example.nextune_backend.service.AlbumGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/album-genre")
@RequiredArgsConstructor
public class AlbumGenreController {

    private final AlbumGenreService albumGenreService;

    @PostMapping
    public ResponseEntity<AlbumGenreResponse> addGenreToAlbum(@RequestBody AlbumGenreRequest request) {
        return ResponseEntity.ok(albumGenreService.addGenreToAlbum(request));
    }

    @DeleteMapping("/{albumId}/{genreId}")
    public ResponseEntity<String> removeGenreFromAlbum(@PathVariable String albumId,
                                                       @PathVariable String genreId) {
        albumGenreService.removeGenreFromAlbum(albumId, genreId);
        return ResponseEntity.ok("Removed genre " + genreId + " from album " + albumId);
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<List<AlbumGenreResponse>> getGenresByAlbum(@PathVariable String albumId) {
        return ResponseEntity.ok(albumGenreService.getGenresByAlbum(albumId));
    }
}
