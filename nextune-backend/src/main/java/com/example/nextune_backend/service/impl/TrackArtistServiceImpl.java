package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.TrackArtistRequest;
import com.example.nextune_backend.dto.response.ProfileResponse;
import com.example.nextune_backend.dto.response.TrackArtistResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.TrackArtist;
import com.example.nextune_backend.entity.TrackArtistId;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.mapper.TrackArtistMapper;
import com.example.nextune_backend.repository.TrackArtistRepository;
import com.example.nextune_backend.repository.TrackRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.TrackArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackArtistServiceImpl implements TrackArtistService {

    private final TrackArtistRepository trackArtistRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final TrackArtistMapper trackArtistMapper;

    @Override
    public TrackArtistResponse assignArtistToTrack(TrackArtistRequest request) {
        Track track = trackRepository.findById(request.getTrackId())
                .orElseThrow(() -> new RuntimeException("Track not found"));
        User artist = userRepository.findById(request.getArtistId())
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        TrackArtist trackArtist = trackArtistMapper.toEntity(request, track, artist);
        trackArtistRepository.save(trackArtist);

        return trackArtistMapper.toResponse(trackArtist);
    }

    @Override
    public TrackArtistResponse updateArtistRole(TrackArtistRequest request) {
        TrackArtistId id = new TrackArtistId(request.getTrackId(), request.getArtistId());
        TrackArtist trackArtist = trackArtistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("TrackArtist not found"));

        trackArtistMapper.updateFromRequest(request, trackArtist);
        trackArtistRepository.save(trackArtist);

        return trackArtistMapper.toResponse(trackArtist);
    }

    @Override
    public void removeArtistFromTrack(String trackId, String artistId) {
        TrackArtistId id = new TrackArtistId(trackId, artistId);
        if (!trackArtistRepository.existsById(id)) {
            throw new RuntimeException("Artist not assigned to this track");
        }
        trackArtistRepository.deleteById(id);
    }

    @Override
    public List<TrackArtistResponse> getArtistsByTrack(String trackId) {
        return trackArtistMapper.toResponseList(trackArtistRepository.findByTrack_Id(trackId));
    }

    @Override
    public List<TrackArtistResponse> getTracksByArtist(String artistId) {
        return trackArtistMapper.toResponseList(trackArtistRepository.findByArtist_Id(artistId));
    }
}
