package com.example.nextune_backend.service;


import com.example.nextune_backend.dto.request.AlbumRequest;
import com.example.nextune_backend.dto.response.AlbumResponse;
import com.example.nextune_backend.entity.Album;

import java.util.List;

public interface AlbumService {
    AlbumResponse createAlbum(AlbumRequest request);
    AlbumResponse updateAlbum(String id, AlbumRequest updatedAlbum);
    void deleteAlbum(String id);
    AlbumResponse getAlbumById(String id);
    List<AlbumResponse> getAllAlbums();
    List<AlbumResponse> getAlbumsByArtist(String artistId);
}
