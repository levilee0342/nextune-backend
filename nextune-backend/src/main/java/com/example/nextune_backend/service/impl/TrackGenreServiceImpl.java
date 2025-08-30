package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.TrackGenreRequest;
import com.example.nextune_backend.dto.response.TrackGenreResponse;
import com.example.nextune_backend.entity.Genre;
import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.TrackGenre;
import com.example.nextune_backend.entity.TrackGenreId;
import com.example.nextune_backend.mapper.TrackGenreMapper;
import com.example.nextune_backend.repository.GenreRepository;
import com.example.nextune_backend.repository.TrackGenreRepository;
import com.example.nextune_backend.repository.TrackRepository;
import com.example.nextune_backend.service.TrackGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrackGenreServiceImpl implements TrackGenreService {

    private final TrackGenreRepository trackGenreRepository;
    private final TrackRepository trackRepository;
    private final GenreRepository genreRepository;
    private final TrackGenreMapper trackGenreMapper;

    @Override
    public TrackGenreResponse addGenreToTrack(TrackGenreRequest request) {
        Track track = trackRepository.findById(request.getTrackId())
                .orElseThrow(() -> new RuntimeException("Track not found"));

        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        TrackGenreId id = new TrackGenreId(request.getTrackId(), request.getGenreId());
        if (trackGenreRepository.existsById(id)) {
            throw new RuntimeException("Genre already exists in track");
        }

        TrackGenre entity = trackGenreMapper.toEntity(request, track, genre);
        return trackGenreMapper.toResponse(trackGenreRepository.save(entity));
    }

    @Override
    public void removeGenreFromTrack(String trackId, String genreId) {
        TrackGenreId id = new TrackGenreId(trackId, genreId);
        if (!trackGenreRepository.existsById(id)) {
            throw new RuntimeException("Genre not found in track");
        }
        trackGenreRepository.deleteById(id);
    }

    @Override
    public List<TrackGenreResponse> getGenresByTrack(String trackId) {
        List<TrackGenre> entities = trackGenreRepository.findByTrack_Id(trackId);
        return trackGenreMapper.toResponseList(entities);
    }
}

