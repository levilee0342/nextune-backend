package com.example.nextune_backend.service;


import com.example.nextune_backend.dto.request.AlbumRequest;
import com.example.nextune_backend.dto.response.AlbumResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.Album;
import com.example.nextune_backend.entity.enums.EntityType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AlbumService {
    AlbumResponse createAlbum(AlbumRequest request);
    AlbumResponse updateAlbum(String id, AlbumRequest updatedAlbum);
    void deleteAlbum(String id);
    AlbumResponse getAlbumById(String id);
    List<AlbumResponse> getAllAlbums();
    List<AlbumResponse> getAlbumsByArtist(String artistId);
    AlbumResponse getAlbumByIdForAdmin(String id);
    List<AlbumResponse> getAllAlbumsForAdmin();
    List<AlbumResponse> getAlbumsByArtistForAdmin(String artistId);
    List<AlbumResponse> searchAlbums(
            String name,
            String genre,
            EntityType entityType,   // SONGS | PODCASTS
            String artistId,
            String sortBy,           // listenCount | createdAt
            String order,            // asc | desc
            int limit
    );
    List<TrackResponse> getAlbumTracks(String albumId, String sortBy, String order);

    Page<TrackResponse> getAlbumTracksPage(String albumId, String sortBy, String order, int page, int size);
    String removeTrackFromAlbum(String albumId, String trackId);
    String addTrackToAlbum(String albumId, String trackId, Integer trackOrder);
}
