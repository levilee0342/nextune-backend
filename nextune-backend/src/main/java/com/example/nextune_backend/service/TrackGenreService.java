package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.TrackGenreRequest;
import com.example.nextune_backend.dto.response.TrackGenreResponse;

import java.util.List;

public interface TrackGenreService {
    TrackGenreResponse addGenreToTrack(TrackGenreRequest request);
    void removeGenreFromTrack(String trackId, String genreId);
    List<TrackGenreResponse> getGenresByTrack(String trackId);
}

