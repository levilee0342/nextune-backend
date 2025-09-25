package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.TrackPlayRequest;
import com.example.nextune_backend.dto.request.TrackRequest;
import com.example.nextune_backend.dto.response.TrackPlayResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.enums.EntityType;

import java.util.List;

public interface TrackService {
    TrackResponse createTrack(TrackRequest request);
    TrackResponse updateTrack(String id, TrackRequest request);
    void deleteTrack(String id);
    TrackResponse getTrackByIdResponse(String id); // đổi tên cho rõ ràng
    List<TrackResponse> getAllTracks();
    TrackResponse getTrackByIdResponseForAdmin(String id); // đổi tên cho rõ ràng
    List<TrackResponse> getAllTracksForAdmin();
    List<TrackResponse> getTracksByAlbum(String albumId);
    List<TrackResponse> getTracksByAlbumForAdmin(String albumId);
    TrackResponse updateStatus(String trackId, String status);
    TrackResponse incrementPlayCount(String trackId);
    TrackPlayResponse playSong(String songId, TrackPlayRequest request);
    List<TrackResponse> searchTracks(
            String name,
            String genre,
            EntityType entityType,
            String artistId,
            String sortBy,
            String order,
            int limit
    );
    List<TrackResponse> getRandomTracks(EntityType entityType, int limit);
    public TrackPlayResponse stopSong(String songId, TrackPlayRequest request);

    public List<TrackResponse> getTracksByIds(List<String> ids);
    public void publishScheduledTracks();

}
