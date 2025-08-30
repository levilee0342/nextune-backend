package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.PlaylistGenreRequest;
import com.example.nextune_backend.dto.request.TrackCollectionRequest;
import com.example.nextune_backend.dto.response.PlaylistGenreResponse;
import com.example.nextune_backend.dto.response.TrackCollectionResponse;
import com.example.nextune_backend.service.PlaylistGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlist-genre")
@RequiredArgsConstructor
public class PlaylistGenreController {

    private final PlaylistGenreService playlistGenreService;

    @PostMapping
    public ResponseEntity<PlaylistGenreResponse> addGenreToPlaylist(@RequestBody PlaylistGenreRequest request) {
        return ResponseEntity.ok(playlistGenreService.addGenreToPlaylist(request));
    }

    @DeleteMapping("/{playlistId}/{genreId}")
    public ResponseEntity<String> removeGenreFromPlaylist(@PathVariable String playlistId,
                                                          @PathVariable String genreId) {
        playlistGenreService.removeGenreFromPlaylist(playlistId, genreId);
        return ResponseEntity.ok("Removed genre " + genreId + " from playlist " + playlistId);
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<List<PlaylistGenreResponse>> getGenresByPlaylist(@PathVariable String playlistId) {
        return ResponseEntity.ok(playlistGenreService.getGenresByPlaylist(playlistId));
    }
}
