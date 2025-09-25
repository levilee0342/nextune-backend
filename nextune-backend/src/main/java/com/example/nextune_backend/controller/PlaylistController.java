package com.example.nextune_backend.controller;


import com.example.nextune_backend.dto.request.PlaylistRequest;
import com.example.nextune_backend.dto.response.PlaylistResponse;
import com.example.nextune_backend.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping
    public ResponseEntity<PlaylistResponse> createPlaylist(@RequestBody PlaylistRequest request) {
        return ResponseEntity.status(201).body(playlistService.createPlaylist(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistResponse> getPlaylistById(@PathVariable String id) {
        return ResponseEntity.ok(playlistService.getPlaylistById(id));
    }

    @GetMapping("/for-admin/{id}")
    public ResponseEntity<PlaylistResponse> getPlaylistByIdForAdmin(@PathVariable String id) {
        return ResponseEntity.ok(playlistService.getPlaylistByIdForAdmin(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<PlaylistResponse>> getAllPlaylists() {
        return ResponseEntity.ok(playlistService.getAllPlaylists());
    }

    @GetMapping("/for-admin/all")
    public ResponseEntity<List<PlaylistResponse>> getAllPlaylistsForAdmin() {
        return ResponseEntity.ok(playlistService.getAllPlaylistsForAdmin());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistResponse> updatePlaylist(
            @PathVariable String id,
            @RequestBody PlaylistRequest request) {
        return ResponseEntity.ok(playlistService.updatePlaylist(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlaylist(@PathVariable String id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.ok("Deleted playlist with id: " + id);
    }

    @GetMapping("/me")
    public ResponseEntity<List<PlaylistResponse>> getMyPlaylists() {
        return ResponseEntity.ok(playlistService.getMyPlaylists());
    }

    @GetMapping(params = {"sortBy"})
    public ResponseEntity<List<PlaylistResponse>> searchPlaylists(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String genre,
            @RequestParam String sortBy,                        // listenCount | createdAt
            @RequestParam(defaultValue = "desc") String order,  // asc | desc
            @RequestParam(defaultValue = "20") int limit
    ) {
        List<PlaylistResponse> data =
                playlistService.searchPlaylists(name, genre, sortBy, order, limit);
        return ResponseEntity.ok(data);
    }


}
