package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.TrackRequest;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.Album;
import com.example.nextune_backend.entity.Status;
import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.mapper.TrackMapper;
import com.example.nextune_backend.repository.AlbumRepository;
import com.example.nextune_backend.repository.TrackRepository;
import com.example.nextune_backend.service.TrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final TrackMapper trackMapper;

    @Override
    public TrackResponse createTrack(TrackRequest request) {
        Album album = getAlbum(request.getAlbumId());
        Track track = trackMapper.map(request, album);
        track.setPublishedAt(LocalDateTime.now());
        track.setPlayCount(0L);
        track.setExplicit(false);
        trackRepository.save(track);
        return trackMapper.map(track);
    }

    @Override
    public TrackResponse updateTrack(String id, TrackRequest request) {
        Track track = getTrackById(id);

        if (request.getAlbumId() != null) {
            Album album = getAlbum(request.getAlbumId());
            track.setAlbum(album);
        }

        trackMapper.updateTrackFromRequest(request, track);
        trackRepository.save(track);

        return trackMapper.map(track);
    }

    @Override
    public void deleteTrack(String id) {
        Track track = getTrackById(id);
        track.setStatus(Status.DELETED);
        trackRepository.save(track);
    }

    @Override
    public TrackResponse getTrackByIdResponse(String id) {
        return trackMapper.map(getTrackById(id));
    }

    @Override
    public List<TrackResponse> getAllTracks() {
        return trackMapper.map(trackRepository.findAllByStatusNot(Status.DELETED));
    }

    @Override
    public List<TrackResponse> getTracksByAlbum(String albumId) {
        return trackMapper.map(trackRepository.findByAlbum_IdAndStatusNot(albumId, Status.DELETED));
    }

    @Override
    public TrackResponse updateStatus(String trackId, String statusStr) {
        Track track = getTrackById(trackId);
        track.setStatus(Status.valueOf(statusStr.toUpperCase()));
        trackRepository.save(track);
        return trackMapper.map(track);
    }

    @Override
    public TrackResponse incrementPlayCount(String trackId) {
        Track track = getTrackById(trackId);
        track.setPlayCount(track.getPlayCount() + 1);
        trackRepository.save(track);
        return trackMapper.map(track);
    }

    private Track getTrackById(String id) {
        return trackRepository.findByIdAndStatusNot(id, Status.DELETED)
                .orElseThrow(() -> new RuntimeException("Track not found or deleted"));
    }

    private Album getAlbum(String albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));
    }
}
