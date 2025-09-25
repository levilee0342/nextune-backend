package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.PlaylistSaveRequest;
import com.example.nextune_backend.dto.request.TrackInSavedPlaylistRequest;
import com.example.nextune_backend.dto.response.PlaylistResponse;
import com.example.nextune_backend.dto.response.PlaylistSaveResponse;
import com.example.nextune_backend.dto.response.TrackInSavedPlaylistResponse;
import com.example.nextune_backend.dto.response.TrackResponse;

import java.util.List;

public interface PlaylistSaveService {
    PlaylistSaveResponse saveForCurrentUser(PlaylistSaveRequest request);
    List<PlaylistSaveResponse> getMySavedPlaylists();
    void unsaveForCurrentUser(String playlistId);

    List<PlaylistSaveResponse> getSaversByPlaylist(String playlistId);
    List<TrackInSavedPlaylistResponse> getTracksFromUserSavedPlaylist(String userId, String playlistId);

}
