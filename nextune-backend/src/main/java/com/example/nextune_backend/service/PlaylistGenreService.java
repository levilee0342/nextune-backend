package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.PlaylistGenreRequest;
import com.example.nextune_backend.dto.response.PlaylistGenreResponse;

import java.util.List;

public interface PlaylistGenreService {
    PlaylistGenreResponse addGenreToPlaylist(PlaylistGenreRequest request);
    void removeGenreFromPlaylist(String playlistId, String genreId);
    List<PlaylistGenreResponse> getGenresByPlaylist(String playlistId);
}
