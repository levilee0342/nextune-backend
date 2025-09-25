package com.example.nextune_backend.service;


import com.example.nextune_backend.dto.CloudAsset;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import com.example.nextune_backend.dto.response.CloudinaryResponse;

public interface CloudinaryService {
    // ===================== USER =====================
    CloudAsset uploadUserAvatar(String userId, MultipartFile file) throws IOException;
    void deleteUserAvatar(String userId) throws IOException;

    CloudAsset uploadUserCover(String userId, MultipartFile file) throws IOException;
    void deleteUserCover(String userId) throws IOException;

    // ===================== SONG =====================
    CloudAsset uploadSongCover(String songId, MultipartFile file, boolean isGif) throws IOException;
    void deleteSongCover(String songId, boolean isGif) throws IOException;
    // ===================== ALBUM =====================
    CloudAsset uploadAlbumCover(String albumId, MultipartFile file) throws IOException;
    void deleteAlbumCover(String albumId) throws IOException;
    // ===================== TRACKS =====================
    CloudAsset uploadTrack(String userId, MultipartFile file) throws IOException;
    void deleteTrack(String publicId) throws IOException;
}
