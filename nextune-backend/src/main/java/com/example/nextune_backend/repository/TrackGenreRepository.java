package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.TrackGenre;
import com.example.nextune_backend.entity.TrackGenreId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackGenreRepository extends JpaRepository<TrackGenre, TrackGenreId> {
    List<TrackGenre> findByTrack_Id(String trackId);
}

