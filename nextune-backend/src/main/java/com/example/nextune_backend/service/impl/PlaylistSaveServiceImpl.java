package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.PlaylistSaveRequest;
import com.example.nextune_backend.dto.request.TrackInSavedPlaylistRequest;
import com.example.nextune_backend.dto.response.*;
import com.example.nextune_backend.entity.*;
import com.example.nextune_backend.mapper.PlaylistMapper;
import com.example.nextune_backend.mapper.PlaylistSaveMapper;
import com.example.nextune_backend.mapper.TrackCollectionMapper;
import com.example.nextune_backend.repository.PlaylistRepository;
import com.example.nextune_backend.repository.PlaylistSaveRepository;
import com.example.nextune_backend.repository.TrackCollectionRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.PlaylistSaveService;
import com.example.nextune_backend.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistSaveServiceImpl implements PlaylistSaveService {

    private final PlaylistSaveRepository playlistSaveRepository;
    private final UserRepository userRepository;
    private final PlaylistRepository playlistRepository;
    private final UserUtility userUtility;
    private final PlaylistSaveMapper playlistSaveMapper;
    private final TrackCollectionRepository trackCollectionRepository;
    private final PlaylistMapper playlistMapper;

    @Override
    @Transactional
    public PlaylistSaveResponse saveForCurrentUser(PlaylistSaveRequest request) {
        final String userId = userUtility.getCurrentUserId();
        final String playlistId = request.getPlaylistId();

        if (playlistSaveRepository.existsByUser_IdAndPlaylist_Id(userId, playlistId)) {
            PlaylistSave existed = playlistSaveRepository
                    .findByUser_IdAndPlaylist_Id(userId, playlistId)
                    .orElseThrow(() -> new RuntimeException("Save state not found after exists check")); // hiếm khi xảy ra
            return playlistSaveMapper.toResponse(existed);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        PlaylistSave entity = playlistSaveMapper.toEntity(user, playlist);
        entity = playlistSaveRepository.save(entity);

        // playlist.setTotalFollowers( (playlist.getTotalFollowers()==null?0:playlist.getTotalFollowers()) + 1 );
        // playlistRepository.save(playlist);

        return playlistSaveMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaylistSaveResponse> getMySavedPlaylists() {
        String userId = userUtility.getCurrentUserId();
        List<PlaylistSave> saves = playlistSaveRepository.findByUser_Id(userId);
        return playlistSaveMapper.toResponseList(saves);
    }

    @Override
    @Transactional
    public void unsaveForCurrentUser(String playlistId) {
        String userId = userUtility.getCurrentUserId();

        // Nếu muốn idempotent: xóa nếu tồn tại, không thì thôi
        playlistSaveRepository.deleteByUser_IdAndPlaylist_Id(userId, playlistId);

        // Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
        // if (playlist != null && playlist.getTotalFollowers() != null && playlist.getTotalFollowers() > 0) {
        //     playlist.setTotalFollowers(playlist.getTotalFollowers() - 1);
        //     playlistRepository.save(playlist);
        // }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaylistSaveResponse> getSaversByPlaylist(String playlistId) {
        List<PlaylistSave> saves = playlistSaveRepository.findByPlaylist_Id(playlistId);
        return playlistSaveMapper.toResponseList(saves);
    }

    @Override
    public List<TrackInSavedPlaylistResponse> getTracksFromUserSavedPlaylist(String userId, String playlistId) {


        boolean isSaved = playlistSaveRepository.existsByUser_IdAndPlaylist_Id(userId, playlistId);
        if (!isSaved) {
            throw new IllegalArgumentException("User chưa lưu playlist này.");
        }

        List<Track> tracks = trackCollectionRepository.findTracksByPlaylistId(playlistId);

        return tracks.stream()
                .map(track -> TrackInSavedPlaylistResponse.builder()
                        .id(track.getId())
                        .name(track.getName())
                        .duration(track.getDuration())
                        .imgUrl(track.getImgUrl())
                        .trackUrl(track.getTrackUrl())
                        .entityType(track.getEntityType())
                        .status(track.getStatus())
                        .albumId(track.getAlbum() != null ? track.getAlbum().getId() : null)
                        .albumName(track.getAlbum() != null ? track.getAlbum().getName() : null)
                        .build()
                )
                .toList();
    }


}
