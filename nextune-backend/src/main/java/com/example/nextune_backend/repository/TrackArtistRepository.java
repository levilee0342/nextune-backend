package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.TrackArtist;
import com.example.nextune_backend.entity.TrackArtistId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackArtistRepository extends JpaRepository<TrackArtist, TrackArtistId> {
    List<TrackArtist> findByTrack_Id(String trackId);
    List<TrackArtist> findByArtist_Id(String artistId);
}
