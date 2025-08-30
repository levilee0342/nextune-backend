package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.TrackCollectionRequest;
import com.example.nextune_backend.dto.response.TrackCollectionResponse;
import com.example.nextune_backend.service.TrackCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/track-collections")
@RequiredArgsConstructor
public class TrackCollectionController {

    private final TrackCollectionService trackCollectionService;

    @PostMapping
    public ResponseEntity<TrackCollectionResponse> createTrackCollection(@RequestBody TrackCollectionRequest request) {
        return ResponseEntity.ok(trackCollectionService.addTrackToPlaylist(request));
    }

    @DeleteMapping("/{playlistId}/{trackId}")
    public ResponseEntity<String> removeTrackFromPlaylist(@PathVariable String playlistId,
                                                          @PathVariable String trackId) {
        trackCollectionService.removeTrackFromPlaylist(playlistId, trackId);
        return ResponseEntity.ok("Removed track " + trackId + " from playlist " + playlistId);
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<List<TrackCollectionResponse>> getTracksByPlaylist(@PathVariable String playlistId) {
        return ResponseEntity.ok(trackCollectionService.getTracksByPlaylist(playlistId));
    }
}
