package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.AlbumRequest;
import com.example.nextune_backend.dto.response.AlbumResponse;
import com.example.nextune_backend.entity.Album;
import com.example.nextune_backend.entity.Status;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.mapper.AlbumMapper;
import com.example.nextune_backend.repository.AlbumRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final AlbumMapper albumMapper;
    @Override
    public AlbumResponse createAlbum(AlbumRequest request) {
        User artist = userRepository.findById(request.getArtistId())
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        Album album = albumMapper.map(request);
        album.setArtist(artist);

        albumRepository.save(album);

        AlbumResponse albumResponse = albumMapper.map(album);
        albumResponse.setArtistId(artist.getId());
        return albumResponse;
    }

    @Override
    public AlbumResponse updateAlbum(String id, AlbumRequest updatedAlbum) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        // Dùng mapper để copy các field không null
        albumMapper.updateAlbumFromRequest(updatedAlbum, album);
       // Nếu muốn update artist theo artistId mới
        if (updatedAlbum.getArtistId() != null) {
            User artist = userRepository.findById(updatedAlbum.getArtistId())
                    .orElseThrow(() -> new RuntimeException("Artist not found"));
            album.setArtist(artist);
        }

        albumRepository.save(album);

        AlbumResponse albumResponse = albumMapper.map(album);
        albumResponse.setArtistId(album.getArtist().getId());
        return albumMapper.map(album);
    }


    @Override
    public void deleteAlbum(String id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));
        album.setStatus(Status.DELETED);
        albumRepository.save(album);
    }

    @Override
    public AlbumResponse getAlbumById(String id) {
        Album album = albumRepository.findByIdAndStatusNot(id, Status.DELETED)
                .orElseThrow(() -> new RuntimeException("Album not found or deleted"));
        return albumMapper.map(album);
    }

    @Override
    public List<AlbumResponse> getAllAlbums() {
        return albumMapper.map(albumRepository.findAllByStatusNot(Status.DELETED));
    }

    @Override
    public List<AlbumResponse> getAlbumsByArtist(String artistId) {
        return albumMapper.map(albumRepository.findByArtist_IdAndStatusNot(artistId, Status.DELETED));
    }
}
