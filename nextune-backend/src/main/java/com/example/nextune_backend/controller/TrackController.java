package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.TrackIdsRequest;
import com.example.nextune_backend.dto.request.TrackPlayRequest;
import com.example.nextune_backend.dto.request.TrackRequest;
import com.example.nextune_backend.dto.response.TrackPlayResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.enums.EntityType;
import com.example.nextune_backend.service.PlayerService;
import com.example.nextune_backend.service.TrackService;
import com.example.nextune_backend.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tracks")
@RequiredArgsConstructor
public class TrackController {

    private final TrackService trackService;
    private final PlayerService playerService;

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

    @GetMapping("/all")
    public ResponseEntity<List<TrackResponse>> getAllTracks() {
        List<TrackResponse> tracks = trackService.getAllTracks();
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/for-admin/{id}")
    public ResponseEntity<TrackResponse> getTrackByIdForAdmin(@PathVariable String id) {
        TrackResponse track = trackService.getTrackByIdResponseForAdmin(id);
        return ResponseEntity.ok(track);
    }

    @GetMapping("/for-admin/all")
    public ResponseEntity<List<TrackResponse>> getAllTracksForAdmin() {
        List<TrackResponse> tracks = trackService.getAllTracksForAdmin();
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

    @PostMapping("/play/{songId}")
    public ResponseEntity<TrackPlayResponse> playSong(
            @PathVariable String songId,
            @RequestBody(required = false) TrackPlayRequest request) {

        System.out.println(">>> [Controller] songId = {}" + songId);
        System.out.println(">>> [Controller] request = {}" + request);


        TrackPlayResponse response = trackService.playSong(songId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/stop/{songId}")
    public ResponseEntity<TrackPlayResponse> stopSong(
            @PathVariable String songId,
            @RequestBody(required = false) TrackPlayRequest request
    ) {
        TrackPlayResponse response = trackService.stopSong(songId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/player/next")
    public ResponseEntity<TrackPlayResponse> nextTrack() {
        return ResponseEntity.ok(playerService.next());
    }

    @PostMapping("/player/prev")
    public ResponseEntity<TrackPlayResponse> prevTrack() {
        return ResponseEntity.ok(playerService.prev());
    }


    @GetMapping(params = {"sortBy"})
    public ResponseEntity<List<TrackResponse>> searchTracks(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) EntityType entityType, // SONGS | PODCASTS
            @RequestParam(required = false) String artistId,
            @RequestParam String sortBy,                           // listenCount | createdAt
            @RequestParam(defaultValue = "desc") String order,     // asc | desc
            @RequestParam(defaultValue = "20") int limit
    ) {
        List<TrackResponse> tracks = trackService.searchTracks(name, genre, entityType, artistId,sortBy, order, limit);
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/random")
    public ResponseEntity<List<TrackResponse>> getRandomTracks(
            @RequestParam(required = false) EntityType entityType,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(trackService.getRandomTracks(entityType, limit));
    }

    @PostMapping("/batch")
    public List<TrackResponse> getTracksByIds(@RequestBody TrackIdsRequest request) {
        return trackService.getTracksByIds(request.getIds());
    }

}
