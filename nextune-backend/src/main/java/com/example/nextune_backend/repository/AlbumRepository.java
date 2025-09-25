package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.Album;
import com.example.nextune_backend.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, String>, JpaSpecificationExecutor<Album> {
    List<Album> findAllByStatus(Status status);
    List<Album> findByArtist_IdAndStatus(String artistId, Status status);
    Optional<Album> findByIdAndStatus(String id, Status status);
    List<Album> findAllByStatusNot(Status status);
    List<Album> findByArtist_IdAndStatusNot(String artistId, Status status);
    Optional<Album> findByIdAndStatusNot(String id, Status status);
}
