package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.AlbumGenre;
import com.example.nextune_backend.entity.AlbumGenreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AlbumGenreRepository extends JpaRepository<AlbumGenre, AlbumGenreId> {
    List<AlbumGenre> findByAlbum_Id(String albumId);
    @Query("SELECT ag FROM AlbumGenre ag JOIN FETCH ag.genre g JOIN FETCH ag.album a WHERE a.id = :albumId")
    List<AlbumGenre> findByAlbumIdWithGenres(@Param("albumId") String albumId);
}
