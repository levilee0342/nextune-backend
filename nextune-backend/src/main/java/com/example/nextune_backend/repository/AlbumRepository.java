package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.Album;
import com.example.nextune_backend.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, String> {
    List<Album> findAllByStatusNot(Status status);
    List<Album> findByArtist_IdAndStatusNot(String artistId, Status status);
    Optional<Album> findByIdAndStatusNot(String id, Status status);
}
