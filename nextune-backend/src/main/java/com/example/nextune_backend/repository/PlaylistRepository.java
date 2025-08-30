package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.Playlist;
import com.example.nextune_backend.entity.Status;
import com.example.nextune_backend.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, String> {
    List<Playlist> findAllByStatusNot(Status status);
    Optional<Playlist> findByIdAndStatusNot(String id, Status status);
}
