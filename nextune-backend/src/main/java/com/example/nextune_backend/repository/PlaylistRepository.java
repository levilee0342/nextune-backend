package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.Playlist;
import com.example.nextune_backend.entity.PlaylistSave;
import com.example.nextune_backend.entity.enums.Status;
import com.example.nextune_backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, String>, JpaSpecificationExecutor<Playlist> {
    List<Playlist> findAllByStatus(Status status);
    Optional<Playlist> findByIdAndStatus(String id, Status status);
    List<Playlist> findAllByStatusNot(Status status);
    Optional<Playlist> findByIdAndStatusNot(String id, Status status);
    int countByUser(User user);
    int countByUserAndStatus(User user, Status status);
    List<Playlist> findAllByUserAndStatusNot(User user, Status status);
    List<Playlist> findAllByUserAndStatusAndDeletedAtAfter(
            User user, Status status, LocalDateTime after);
    Optional<Playlist> findByIdAndUser_IdAndStatus(String id, String userId, Status status);




}
