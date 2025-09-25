package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.PlaylistRequest;
import com.example.nextune_backend.dto.response.PlaylistResponse;
import com.example.nextune_backend.dto.response.TrackResponse;

import java.util.List;

public interface PlaylistService {
    PlaylistResponse createPlaylist(PlaylistRequest request);

    PlaylistResponse updatePlaylist(String id, PlaylistRequest request);

    void deletePlaylist(String id);

    PlaylistResponse getPlaylistById(String id);
    PlaylistResponse getPlaylistByIdForAdmin(String id);

    List<PlaylistResponse> getAllPlaylists();
    List<PlaylistResponse> getAllPlaylistsForAdmin();

    List<PlaylistResponse> getMyPlaylists();

    List<PlaylistResponse> getMyDeletedPlaylistsEligible(Integer days);
    PlaylistResponse recoverMyPlaylist(String id);
    List<PlaylistResponse> searchPlaylists(
            String name,
            String genre,      // ví dụ: "rap"
            String sortBy,     // listenCount | createdAt
            String order,      // asc | desc
            int limit
    );




}
