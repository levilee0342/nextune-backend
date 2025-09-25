package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.TrackCollectionRequest;
import com.example.nextune_backend.dto.response.TrackCollectionResponse;

import java.util.List;

public interface TrackCollectionService {

    List<TrackCollectionResponse> getTracksByPlaylist(String playlistId);

    String addTrackToPlaylists(TrackCollectionRequest request);
    String removeTrackFromPlaylists(TrackCollectionRequest request);
    List<TrackCollectionResponse> getPlaylistsByTrack(String trackId);

}
