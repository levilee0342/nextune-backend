package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.CloudAsset;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import com.example.nextune_backend.dto.response.CloudinaryResponse;
import com.example.nextune_backend.service.CloudinaryService;

@RestController
@RequestMapping("/cloudinary")
@RequiredArgsConstructor
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;

    // ===================== USER =====================
    @PostMapping("/users/{userId}/avatar")
    public ResponseEntity<CloudAsset> uploadUserAvatar(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(cloudinaryService.uploadUserAvatar(userId, file));
    }

    @DeleteMapping("/users/{userId}/avatar")
    public ResponseEntity<Void> deleteUserAvatar(@PathVariable String userId) throws IOException {
        cloudinaryService.deleteUserAvatar(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}/cover")
    public ResponseEntity<CloudAsset> uploadUserCover(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(cloudinaryService.uploadUserCover(userId, file));
    }

    @DeleteMapping("/users/{userId}/cover")
    public ResponseEntity<Void> deleteUserCover(@PathVariable String userId) throws IOException {
        cloudinaryService.deleteUserCover(userId);
        return ResponseEntity.noContent().build();
    }

    // ===================== SONG (supports image + gif) =====================
    /**
     * Upload song cover.
     * Use query param ?gif=true for gif (animated), default is static image.
     */
    @PostMapping("/songs/{songId}/cover")
    public ResponseEntity<CloudAsset> uploadSongCover(
            @PathVariable String songId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "gif", defaultValue = "false") boolean isGif
    ) throws IOException {
        return ResponseEntity.ok(cloudinaryService.uploadSongCover(songId, file, isGif));
    }

    /**
     * Delete song cover. Use ?gif=true to delete the gif variant, otherwise deletes the static image.
     */
    @DeleteMapping("/songs/{songId}/cover")
    public ResponseEntity<Void> deleteSongCover(
            @PathVariable String songId,
            @RequestParam(name = "gif", defaultValue = "false") boolean isGif
    ) throws IOException {
        cloudinaryService.deleteSongCover(songId, isGif);
        return ResponseEntity.noContent().build();
    }

    // ===================== ALBUM =====================
    @PostMapping("/albums/{albumId}/cover")
    public ResponseEntity<CloudAsset> uploadAlbumCover(
            @PathVariable String albumId,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(cloudinaryService.uploadAlbumCover(albumId, file));
    }

    @DeleteMapping("/albums/{albumId}/cover")
    public ResponseEntity<Void> deleteAlbumCover(@PathVariable String albumId) throws IOException {
        cloudinaryService.deleteAlbumCover(albumId);
        return ResponseEntity.noContent().build();
    }

    // ===================== ALBUM =====================
    @PostMapping("/upload/{userId}")
    public ResponseEntity<?> uploadTrack(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file) {

        try {
            CloudAsset asset = cloudinaryService.uploadTrack(userId, file);
            return ResponseEntity.ok(asset);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Upload failed: " + e.getMessage());
        }
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<?> deleteTrack(@PathVariable String publicId) {
        try {
            cloudinaryService.deleteTrack(publicId);
            return ResponseEntity.ok("Deleted successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Delete failed: " + e.getMessage());
        }
    }

}
