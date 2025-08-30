package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.TrackGenreRequest;
import com.example.nextune_backend.dto.response.TrackGenreResponse;
import com.example.nextune_backend.service.TrackGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/track-genre")
@RequiredArgsConstructor
public class TrackGenreController {

    private final TrackGenreService trackGenreService;

    @PostMapping
    public ResponseEntity<TrackGenreResponse> addGenreToTrack(@RequestBody TrackGenreRequest request) {
        return ResponseEntity.ok(trackGenreService.addGenreToTrack(request));
    }

    @DeleteMapping("/{trackId}/{genreId}")
    public ResponseEntity<String> removeGenreFromTrack(@PathVariable String trackId,
                                                       @PathVariable String genreId) {
        trackGenreService.removeGenreFromTrack(trackId, genreId);
        return ResponseEntity.ok("Removed genre " + genreId + " from track " + trackId);
    }

    @GetMapping("/{trackId}")
    public ResponseEntity<List<TrackGenreResponse>> getGenresByTrack(@PathVariable String trackId) {
        return ResponseEntity.ok(trackGenreService.getGenresByTrack(trackId));
    }
}

