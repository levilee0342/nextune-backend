package com.example.nextune_backend.service.impl;


import com.example.nextune_backend.dto.request.PlaylistRequest;

import com.example.nextune_backend.dto.response.PlaylistResponse;
import com.example.nextune_backend.entity.Playlist;
import com.example.nextune_backend.entity.Status;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.mapper.PlaylistMapper;
import com.example.nextune_backend.repository.PlaylistRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.PlaylistService;
import com.example.nextune_backend.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserUtility userUtility;
    private final UserRepository userRepository;
    private final PlaylistMapper playlistMapper;

    @Override
    public PlaylistResponse createPlaylist(PlaylistRequest request) {
        User user = userRepository.findById(userUtility.getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Playlist playlist = playlistMapper.toEntity(request, user);
        playlist.setCreatedAt(LocalDateTime.now());
        return playlistMapper.toResponse(playlistRepository.save(playlist));
    }

    @Override
    public PlaylistResponse updatePlaylist(String id, PlaylistRequest request) {
        Playlist playlist = getEntityById(id);
        playlistMapper.updateEntity(request, playlist);
        return playlistMapper.toResponse(playlistRepository.save(playlist));
    }

    @Override
    public void deletePlaylist(String id) {
        Playlist playlist = getEntityById(id);
        playlist.setStatus(Status.DELETED);
        playlistRepository.save(playlist);
    }

    @Override
    public PlaylistResponse getPlaylistById(String id) {
        return playlistMapper.toResponse(getEntityById(id));
    }

    @Override
    public List<PlaylistResponse> getAllPlaylists() {
        return playlistMapper.toResponseList(playlistRepository.findAllByStatusNot(Status.DELETED));
    }

    private Playlist getEntityById(String id) {
        return playlistRepository.findByIdAndStatusNot(id, Status.DELETED)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
    }
}
