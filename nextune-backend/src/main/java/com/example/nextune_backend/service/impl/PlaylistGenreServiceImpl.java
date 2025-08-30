package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.PlaylistGenreRequest;
import com.example.nextune_backend.dto.response.PlaylistGenreResponse;
import com.example.nextune_backend.entity.Genre;
import com.example.nextune_backend.entity.Playlist;
import com.example.nextune_backend.entity.PlaylistGenre;
import com.example.nextune_backend.entity.PlaylistGenreId;
import com.example.nextune_backend.mapper.PlaylistGenreMapper;
import com.example.nextune_backend.repository.GenreRepository;
import com.example.nextune_backend.repository.PlaylistGenreRepository;
import com.example.nextune_backend.repository.PlaylistRepository;
import com.example.nextune_backend.service.PlaylistGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistGenreServiceImpl implements PlaylistGenreService {

    private final PlaylistGenreRepository playlistGenreRepository;
    private final PlaylistRepository playlistRepository;
    private final GenreRepository genreRepository;
    private final PlaylistGenreMapper playlistGenreMapper;

    @Override
    public PlaylistGenreResponse addGenreToPlaylist(PlaylistGenreRequest request) {
        Playlist playlist = playlistRepository.findById(request.getPlaylistId())
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        PlaylistGenreId id = new PlaylistGenreId(request.getPlaylistId(), request.getGenreId());
        if (playlistGenreRepository.existsById(id)) {
            throw new RuntimeException("Genre already exists in playlist");
        }

        PlaylistGenre entity = playlistGenreMapper.toEntity(request, playlist, genre);
        return playlistGenreMapper.toResponse(playlistGenreRepository.save(entity));
    }

    @Override
    public void removeGenreFromPlaylist(String playlistId, String genreId) {
        PlaylistGenreId id = new PlaylistGenreId(playlistId, genreId);
        if (!playlistGenreRepository.existsById(id)) {
            throw new RuntimeException("Genre not found in playlist");
        }
        playlistGenreRepository.deleteById(id);
    }

    @Override
    public List<PlaylistGenreResponse> getGenresByPlaylist(String playlistId) {
        List<PlaylistGenre> entities = playlistGenreRepository.findByPlaylist_Id(playlistId);
        return playlistGenreMapper.toResponseList(entities);
    }
}
