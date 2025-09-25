package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.TrackCollectionRequest;
import com.example.nextune_backend.dto.response.TrackCollectionResponse;
import com.example.nextune_backend.service.TrackCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/track-collections")
@RequiredArgsConstructor
public class TrackCollectionController {

    private final TrackCollectionService trackCollectionService;

    @PostMapping
    public ResponseEntity<Map<String, String>> addTrackToPlaylists(@RequestBody TrackCollectionRequest request) {
        try {
            String result = trackCollectionService.addTrackToPlaylists(request);
            return ResponseEntity.ok(Map.of("Messages", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("Messages", "Error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, String>> removeTrackFromPlaylists(@RequestBody TrackCollectionRequest request) {
        try {
            String result = trackCollectionService.removeTrackFromPlaylists(request);
            return ResponseEntity.ok(Map.of("Messages", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("Messages", "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<List<TrackCollectionResponse>> getTracksByPlaylist(@PathVariable String playlistId) {
        return ResponseEntity.ok(trackCollectionService.getTracksByPlaylist(playlistId));
    }
}
