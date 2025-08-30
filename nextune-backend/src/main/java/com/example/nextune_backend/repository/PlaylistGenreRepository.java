package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.PlaylistGenre;
import com.example.nextune_backend.entity.PlaylistGenreId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistGenreRepository extends JpaRepository<PlaylistGenre, PlaylistGenreId> {
    List<PlaylistGenre> findByPlaylist_Id(String playlistId);
}
