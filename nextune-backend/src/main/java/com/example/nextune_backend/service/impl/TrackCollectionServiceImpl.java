package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.TrackCollectionRequest;
import com.example.nextune_backend.dto.response.TrackCollectionResponse;
import com.example.nextune_backend.entity.*;
import com.example.nextune_backend.mapper.TrackCollectionMapper;
import com.example.nextune_backend.repository.PlaylistRepository;
import com.example.nextune_backend.repository.TrackCollectionRepository;
import com.example.nextune_backend.repository.TrackRepository;
import com.example.nextune_backend.service.TrackCollectionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackCollectionServiceImpl implements TrackCollectionService {

        private final TrackCollectionRepository trackCollectionRepository;
        private final TrackRepository trackRepository;
        private final PlaylistRepository playlistRepository;
        private final TrackCollectionMapper trackCollectionMapper;

        @Override
        @Transactional
        public String  addTrackToPlaylists(TrackCollectionRequest request) {
            String trackId = request.getTrackId();
            List<String> playlistIds = request.getPlaylistIds();
            Track track = trackRepository.findById(trackId)
                    .orElseThrow(() -> new RuntimeException("Track not found"));

            if (playlistIds == null || playlistIds.isEmpty()) {
                return "Added Failed";
            }

            try {
                for (String playlistId : playlistIds) {
                    trackCollectionRepository.insertTrackIntoPlaylist(
                            trackId,
                            playlistId,
                            request.getTrackOrder()

                    );
                    Playlist playlist = playlistRepository.findById(playlistId)
                            .orElseThrow(() -> new RuntimeException("Playlist not found"));

                    playlist.setTotalTracks(playlist.getTotalTracks() + 1);
                    playlist.setTotalDuration(playlist.getTotalDuration() + track.getDuration());
                }
                return "Added Successfully";
            } catch (Exception e) {
                return "Added Failed";
            }
        }

        @Override
        @Transactional
        public String  removeTrackFromPlaylists(TrackCollectionRequest request) {
            String trackId = request.getTrackId();
            List<String> playlistIds = request.getPlaylistIds();

            if (playlistIds == null || playlistIds.isEmpty()) {
                return "Removed Failed";
            }

            try {
                Track track = trackRepository.findById(trackId)
                        .orElseThrow(() -> new RuntimeException("Track not found"));
                trackCollectionRepository.deleteTrackFromPlaylists(trackId, playlistIds);
                for (String playlistId : playlistIds) {
                    Playlist playlist = playlistRepository.findById(playlistId)
                            .orElseThrow(() -> new RuntimeException("Playlist not found"));

                    int newTotalTracks = Math.max(0, playlist.getTotalTracks() - 1);
                    int newTotalDuration = Math.max(0, playlist.getTotalDuration() - track.getDuration());

                    playlist.setTotalTracks(newTotalTracks);
                    playlist.setTotalDuration(newTotalDuration);

                    playlistRepository.save(playlist);
                }
                return "Removed Successfully";
            } catch (Exception e) {
                return "Removed Failed";
            }
        }

        @Override
        public List<TrackCollectionResponse> getTracksByPlaylist(String playlistId) {
            List<TrackCollection> collections = trackCollectionRepository.findPublishedByPlaylistOrdered(playlistId);
            return trackCollectionMapper.toResponseList(collections);
        }

        @Override
        public List<TrackCollectionResponse> getPlaylistsByTrack(String trackId) {
            return trackCollectionRepository.findByTrackId(trackId).stream()
                    .map(trackCollectionMapper::toResponse)
                    .collect(Collectors.toList());
        }

        
}
