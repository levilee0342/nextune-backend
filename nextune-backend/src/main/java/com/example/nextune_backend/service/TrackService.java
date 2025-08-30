package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.TrackRequest;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.Track;

import java.util.List;

public interface TrackService {
    TrackResponse createTrack(TrackRequest request);
    TrackResponse updateTrack(String id, TrackRequest request);
    void deleteTrack(String id);
    TrackResponse getTrackByIdResponse(String id); // đổi tên cho rõ ràng
    List<TrackResponse> getAllTracks();
    List<TrackResponse> getTracksByAlbum(String albumId);
    TrackResponse updateStatus(String trackId, String status);
    TrackResponse incrementPlayCount(String trackId);
}
