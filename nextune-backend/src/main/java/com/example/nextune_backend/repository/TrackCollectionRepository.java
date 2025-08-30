package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.TrackCollection;
import com.example.nextune_backend.entity.TrackCollectionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackCollectionRepository extends JpaRepository<TrackCollection, TrackCollectionId> {
    List<TrackCollection> findByPlaylist_Id(String playlistId);
}
