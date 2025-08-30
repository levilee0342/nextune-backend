package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.AlbumRequest;
import com.example.nextune_backend.dto.response.AlbumResponse;
import com.example.nextune_backend.entity.Album;
import com.example.nextune_backend.service.AlbumService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<List<AlbumResponse>> getAllAlbums() {
        List<AlbumResponse> albums = albumService.getAllAlbums();
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<List<AlbumResponse>> getAlbumsByArtist(@PathVariable String artistId) {
        List<AlbumResponse> albums = albumService.getAlbumsByArtist(artistId);
        return ResponseEntity.ok(albums);
    }
}
