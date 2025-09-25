package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.PlaylistSaveRequest;
import com.example.nextune_backend.dto.request.TrackInSavedPlaylistRequest;
import com.example.nextune_backend.dto.response.*;
import com.example.nextune_backend.service.PlaylistSaveService;
import com.example.nextune_backend.service.PlaylistService;
import com.example.nextune_backend.service.TrackCollectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/playlist-saves")
@RequiredArgsConstructor
public class PlaylistSaveController {

    private final PlaylistSaveService playlistSaveService;
    private final PlaylistService playlistService;
    private final TrackCollectionService trackCollectionService;

    @PostMapping
    public ResponseEntity<PlaylistSaveResponse> saveForCurrentUser(@RequestBody @Valid PlaylistSaveRequest request) {
        PlaylistSaveResponse resp = playlistSaveService.saveForCurrentUser(request);
        return ResponseEntity.status(201).body(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<List<PlaylistSaveResponse>> getMySavedPlaylists() {
        return ResponseEntity.ok(playlistSaveService.getMySavedPlaylists());
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<String> unsaveForCurrentUser(@PathVariable String playlistId) {
        playlistSaveService.unsaveForCurrentUser(playlistId);
        return ResponseEntity.ok("Unsave playlist: " + playlistId + " successfully");
    }

    @GetMapping("/playlist/{playlistId}/savers")
    public ResponseEntity<List<PlaylistSaveResponse>> getSaversByPlaylist(@PathVariable String playlistId) {
        return ResponseEntity.ok(playlistSaveService.getSaversByPlaylist(playlistId));
    }

    @GetMapping("/savedTracks/{userId}/{playlistId}")
    public ResponseEntity<List<TrackInSavedPlaylistResponse>> getTracksFromSavedPlaylist(
            @PathVariable("userId") String userId,
            @PathVariable("playlistId") String playlistId
    ) {
        List<TrackInSavedPlaylistResponse> tracks =
                playlistSaveService.getTracksFromUserSavedPlaylist(userId, playlistId);
        return ResponseEntity.ok(tracks);
    }

    @GetMapping("/savedTracks/{trackId}")
    public ResponseEntity<List<PlaylistResponse>> getSavedPlaylistsContainingTrack(
            @PathVariable String trackId
    ) {
        // 1. Lấy tất cả playlist mà user hiện tại đã save
        List<PlaylistSaveResponse> savedPlaylists = playlistSaveService.getMySavedPlaylists();

        // 2. Lấy tất cả playlist chứa track này
        List<TrackCollectionResponse> trackCollections = trackCollectionService.getPlaylistsByTrack(trackId);

        // 3. Join theo playlistId
        Set<String> trackPlaylistIds = trackCollections.stream()
                .map(TrackCollectionResponse::getPlaylistId)
                .collect(Collectors.toSet());

        List<PlaylistResponse> result = savedPlaylists.stream()
                .filter(save -> trackPlaylistIds.contains(save.getPlaylistId()))
                .map(save -> {
                    PlaylistSaveResponse.PlaylistSummary summary = save.getPlaylist();
                    PlaylistResponse response = new PlaylistResponse();
                    response.setId(summary.getId());
                    response.setName(summary.getName());
                    response.setImgUrl(summary.getImgUrl());
                    response.setTotalTracks(summary.getTotalTracks());
                    response.setTotalDuration(summary.getTotalDuration());
                    response.setTotalFollowers(summary.getTotalFollowers());
                    response.setIsPublic(summary.getIsPublic());

                    // set thêm các field khác nếu PlaylistSummary có đủ data
                    return response;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
