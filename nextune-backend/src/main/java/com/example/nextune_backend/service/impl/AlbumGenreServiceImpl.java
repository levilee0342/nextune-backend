package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.AlbumGenreRequest;
import com.example.nextune_backend.dto.response.AlbumGenreResponse;
import com.example.nextune_backend.entity.Album;
import com.example.nextune_backend.entity.AlbumGenre;
import com.example.nextune_backend.entity.AlbumGenreId;
import com.example.nextune_backend.entity.Genre;
import com.example.nextune_backend.mapper.AlbumGenreMapper;
import com.example.nextune_backend.repository.AlbumGenreRepository;
import com.example.nextune_backend.repository.AlbumRepository;
import com.example.nextune_backend.repository.GenreRepository;
import com.example.nextune_backend.service.AlbumGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumGenreServiceImpl implements AlbumGenreService {

    private final AlbumGenreRepository albumGenreRepository;
    private final AlbumRepository albumRepository;
    private final GenreRepository genreRepository;
    private final AlbumGenreMapper albumGenreMapper;

    @Override
    public AlbumGenreResponse addGenreToAlbum(AlbumGenreRequest request) {
        Album album = albumRepository.findById(request.getAlbumId())
                .orElseThrow(() -> new RuntimeException("Album not found"));

        Genre genre = genreRepository.findById(request.getGenreId())
                .orElseThrow(() -> new RuntimeException("Genre not found"));

        AlbumGenreId id = new AlbumGenreId(request.getAlbumId(), request.getGenreId());
        if (albumGenreRepository.existsById(id)) {
            throw new RuntimeException("Genre already exists in album");
        }

        AlbumGenre entity = albumGenreMapper.toEntity(request, album, genre);
        return albumGenreMapper.toResponse(albumGenreRepository.save(entity));
    }

    @Override
    public void removeGenreFromAlbum(String albumId, String genreId) {
        AlbumGenreId id = new AlbumGenreId(albumId, genreId);
        if (!albumGenreRepository.existsById(id)) {
            throw new RuntimeException("Genre not found in album");
        }
        albumGenreRepository.deleteById(id);
    }

    @Override
    public List<AlbumGenreResponse> getGenresByAlbum(String albumId) {
        List<AlbumGenre> entities = albumGenreRepository.findByAlbum_Id(albumId);
        return albumGenreMapper.toResponseList(entities);
    }
}
