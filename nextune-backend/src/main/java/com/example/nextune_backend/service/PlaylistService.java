package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.PlaylistRequest;
import com.example.nextune_backend.dto.response.PlaylistResponse;
import com.example.nextune_backend.entity.Playlist;

import java.util.List;

public interface PlaylistService {
    PlaylistResponse createPlaylist(PlaylistRequest request);
    PlaylistResponse updatePlaylist(String id, PlaylistRequest request);
    void deletePlaylist(String id);
    PlaylistResponse getPlaylistById(String id);
    List<PlaylistResponse> getAllPlaylists();
}
