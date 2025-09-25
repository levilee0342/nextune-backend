package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.PlaylistSave;
import com.example.nextune_backend.entity.PlaylistSaveId;
import com.example.nextune_backend.service.TrackCollectionService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaylistSaveRepository extends JpaRepository<PlaylistSave, PlaylistSaveId> {

    boolean existsByUser_IdAndPlaylist_Id(String userId, String playlistId);

    Optional<PlaylistSave> findByUser_IdAndPlaylist_Id(String userId, String playlistId);

    void deleteByUser_IdAndPlaylist_Id(String userId, String playlistId);

    List<PlaylistSave> findByUser_Id(String userId);

    List<PlaylistSave> findByPlaylist_Id(String playlistId);

    long countByPlaylist_Id(String playlistId);



    List<PlaylistSave> findAllByUser_Id(String userId);
}
