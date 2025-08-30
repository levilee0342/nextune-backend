package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.TrackCollectionRequest;
import com.example.nextune_backend.dto.response.TrackCollectionResponse;
import com.example.nextune_backend.entity.*;
import com.example.nextune_backend.mapper.TrackCollectionMapper;
import com.example.nextune_backend.repository.PlaylistRepository;
import com.example.nextune_backend.repository.TrackCollectionRepository;
import com.example.nextune_backend.repository.TrackRepository;
import com.example.nextune_backend.service.TrackCollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackCollectionServiceImpl implements TrackCollectionService {

    private final TrackCollectionRepository trackCollectionRepository;
    private final TrackRepository trackRepository;
    private final PlaylistRepository playlistRepository;
    private final TrackCollectionMapper trackCollectionMapper;

    @Override
    public TrackCollectionResponse addTrackToPlaylist(TrackCollectionRequest request) {
        Track track = trackRepository.findById(request.getTrackId())
                .orElseThrow(() -> new RuntimeException("Track not found"));
        Playlist playlist = playlistRepository.findById(request.getPlaylistId())
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        TrackCollectionId id = new TrackCollectionId(request.getPlaylistId(), request.getTrackId());
        if (trackCollectionRepository.existsById(id)) {
            throw new RuntimeException("Track already exists in playlist");
        }

        TrackCollection entity = trackCollectionMapper.toEntity(request, playlist, track);
        return trackCollectionMapper.toResponse(trackCollectionRepository.save(entity));
    }

    @Override
    public void removeTrackFromPlaylist(String playlistId, String trackId) {
        TrackCollectionId id = new TrackCollectionId(trackId,playlistId);
        if (!trackCollectionRepository.existsById(id)) {
            throw new RuntimeException("Track not found in playlist");
        }
        trackCollectionRepository.deleteById(id);
    }

    @Override
    public List<TrackCollectionResponse> getTracksByPlaylist(String playlistId) {
        List<TrackCollection> collections = trackCollectionRepository.findByPlaylist_Id(playlistId);
        return trackCollectionMapper.toResponseList(collections);
    }
}
