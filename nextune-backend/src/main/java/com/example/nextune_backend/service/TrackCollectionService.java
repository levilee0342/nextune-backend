package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.TrackCollectionRequest;
import com.example.nextune_backend.dto.response.TrackCollectionResponse;

import java.util.List;

public interface TrackCollectionService {
    TrackCollectionResponse addTrackToPlaylist(TrackCollectionRequest request);
    void removeTrackFromPlaylist(String playlistId, String trackId);
    List<TrackCollectionResponse> getTracksByPlaylist(String playlistId);
}
