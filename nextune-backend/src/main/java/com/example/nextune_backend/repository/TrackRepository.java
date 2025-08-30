package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.Status;
import com.example.nextune_backend.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, String> {
    List<Track> findAllByStatusNot(Status status);

    List<Track> findByAlbum_IdAndStatusNot(String albumId, Status status);

    Optional<Track> findByIdAndStatusNot(String id, Status status);
}
