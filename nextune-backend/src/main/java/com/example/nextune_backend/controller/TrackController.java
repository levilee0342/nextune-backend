package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.TrackRequest;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;

    @PostMapping
    public ResponseEntity<TrackResponse> createTrack(@RequestBody TrackRequest request) {
        TrackResponse track = trackService.createTrack(request);
        return ResponseEntity.status(201).body(track);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackResponse> getTrackById(@PathVariable String id) {
        TrackResponse track = trackService.getTrackByIdResponse(id);
        return ResponseEntity.ok(track);
    }

    @GetMapping
    public ResponseEntity<List<TrackResponse>> getAllTracks() {
        List<TrackResponse> tracks = trackService.getAllTracks();
        return ResponseEntity.ok(tracks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackResponse> updateTrack(@PathVariable String id, @RequestBody TrackRequest request) {
        TrackResponse track = trackService.updateTrack(id, request);
        return ResponseEntity.ok(track);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTrack(@PathVariable String id) {
        trackService.deleteTrack(id);
        return ResponseEntity.ok("Deleted track with id: " + id);
    }
}
