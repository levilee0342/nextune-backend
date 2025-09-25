package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.AlbumRequest;
import com.example.nextune_backend.dto.response.AlbumResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.Album;
import com.example.nextune_backend.entity.enums.EntityType;
import com.example.nextune_backend.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<AlbumResponse> createAlbum(@RequestBody AlbumRequest request) {
        AlbumResponse album = albumService.createAlbum(request);
        // Trả 201 Created kèm Location header
        return ResponseEntity
                .created(URI.create("/albums/" + album.getId()))
                .body(album);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumResponse> updateAlbum(@PathVariable String id, @RequestBody AlbumRequest albumRequest) {
        AlbumResponse album = albumService.updateAlbum(id, albumRequest);
        return ResponseEntity.ok(album);
    }

    @PutMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable String id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumResponse> getAlbumById(@PathVariable String id) {
        AlbumResponse album = albumService.getAlbumById(id);
        return ResponseEntity.ok(album);
    }

    @GetMapping("/all")
    public ResponseEntity<List<AlbumResponse>> getAllAlbums() {
        List<AlbumResponse> albums = albumService.getAllAlbums();
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<AlbumResponse>> getAlbumsByArtist(@PathVariable String artistId) {
        List<AlbumResponse> albums = albumService.getAlbumsByArtist(artistId);
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/for-admin/{id}")
    public ResponseEntity<AlbumResponse> getAlbumByIdForAdmin(@PathVariable String id) {
        AlbumResponse album = albumService.getAlbumByIdForAdmin(id);
        return ResponseEntity.ok(album);
    }

    @GetMapping("/for-admin/all")
    public ResponseEntity<List<AlbumResponse>> getAllAlbumsForAdmin() {
        List<AlbumResponse> albums = albumService.getAllAlbumsForAdmin();
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/for-admin/artist/{artistId}")
    public ResponseEntity<List<AlbumResponse>> getAlbumsByArtistForAdmin(@PathVariable String artistId) {
        List<AlbumResponse> albums = albumService.getAlbumsByArtistForAdmin(artistId);
        return ResponseEntity.ok(albums);
    }

    @PostMapping("/{albumId}/addTracks/{trackId}")
    public ResponseEntity<String> addTrackToAlbum(
            @PathVariable String albumId,
            @PathVariable String trackId,
            @RequestParam(required = false) Integer trackOrder // optional
    ) {
        String result = albumService.addTrackToAlbum(albumId, trackId, trackOrder);
        if (result.startsWith("Add Failed")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }


    @DeleteMapping("/{albumId}/removeTracks/{trackId}")
    public ResponseEntity<String> removeTrackFromAlbum(
            @PathVariable String albumId,
            @PathVariable String trackId
    ) {
        String result = albumService.removeTrackFromAlbum(albumId, trackId);
        if (result.startsWith("Remove Failed")) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping(params = {"sortBy"})
    public ResponseEntity<List<AlbumResponse>> searchAlbums(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) EntityType entityType, // SONGS | PODCASTS
            @RequestParam(required = false) String artistId,
            @RequestParam String sortBy,                           // listenCount | createdAt
            @RequestParam(defaultValue = "desc") String order,     // asc | desc
            @RequestParam(defaultValue = "20") int limit
    ) {
        List<AlbumResponse> albums = albumService.searchAlbums(name, genre, entityType, artistId, sortBy, order, limit);
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/{id}/tracks")
    public ResponseEntity<List<TrackResponse>> getAlbumTracks(
            @PathVariable String id,
            @RequestParam(required = false) String sortBy,      // name | publishedAt | duration | playCount | trackOrder
            @RequestParam(defaultValue = "asc") String order    // asc | desc
    ) {
        List<TrackResponse> tracks = albumService.getAlbumTracks(id, sortBy, order);
        return ResponseEntity.ok(tracks);
    }

    // Phân trang
    @GetMapping("/{id}/tracks/page")
    public ResponseEntity<Page<TrackResponse>> getAlbumTracksPage(
            @PathVariable String id,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<TrackResponse> tracks = albumService.getAlbumTracksPage(id, sortBy, order, page, size);
        return ResponseEntity.ok(tracks);
    }
}
