package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.TrackArtistRequest;
import com.example.nextune_backend.dto.response.ProfileResponse;
import com.example.nextune_backend.dto.response.TrackArtistResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.TrackArtist;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.service.TrackArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/track-artists")
@RequiredArgsConstructor
public class TrackArtistController {

    private final TrackArtistService trackArtistService;

    @PostMapping
    public ResponseEntity<TrackArtistResponse> assignArtist(@RequestBody TrackArtistRequest request) {
        return ResponseEntity.ok(trackArtistService.assignArtistToTrack(request));
    }

    @PutMapping
    public ResponseEntity<TrackArtistResponse> updateArtistRole(@RequestBody TrackArtistRequest request) {
        return ResponseEntity.ok(trackArtistService.updateArtistRole(request));
    }

    @DeleteMapping("/{trackId}/{artistId}")
    public ResponseEntity<Void> removeArtist(@PathVariable String trackId, @PathVariable String artistId) {
        trackArtistService.removeArtistFromTrack(trackId, artistId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/track/{trackId}")
    public ResponseEntity<List<TrackArtistResponse>> getArtistsByTrack(@PathVariable String trackId) {
        return ResponseEntity.ok(trackArtistService.getArtistsByTrack(trackId));
    }

//    @GetMapping("/artist/{artistId}")
//    public ResponseEntity<List<TrackArtistResponse>> getTracksByArtist(@PathVariable String artistId) {
//        return ResponseEntity.ok(trackArtistService.getTracksByArtist(artistId));
//    }
@GetMapping("/artist/{artistId}")
public ResponseEntity<List<TrackResponse>> getTracksByArtist(@PathVariable String artistId) {
    return ResponseEntity.ok(trackArtistService.getTracksByArtist(artistId));
}

}
