package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.enums.EntityType;
import com.example.nextune_backend.entity.enums.Status;
import com.example.nextune_backend.entity.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, String>, JpaSpecificationExecutor<Track> {
    List<Track> findAllByStatusNot(Status status);

    List<Track> findByAlbum_IdAndStatus(String albumId, Status status);

    List<Track> findAllByStatus(Status status);
    Optional<Track> findByIdAndStatusNot(String id, Status status);
    Optional<Track> findByIdAndStatus(String id, Status status);

    @Query(value = """
        SELECT t.* 
        FROM track t
        WHERE t.status = 'PUBLISHED'
          AND (:entityType IS NULL OR t.entity_type = :entityType)
        ORDER BY RAND()
        LIMIT :limit
        """, nativeQuery = true)
    List<Track> findRandomPublished(@Param("entityType") String entityType, @Param("limit") int limit);

    List<Track> findByAlbum_IdAndStatus(String albumId, Status status, Sort sort);

    Page<Track> findByAlbum_IdAndStatus(String albumId, Status status, Pageable pageable);

    @Modifying
    @Query("UPDATE Track t SET t.isPlaying = false WHERE t.isPlaying = true")
    void resetAllPlaying();

    @Modifying
    @Query("UPDATE Track t SET t.isPlaying = true WHERE t.id = :trackId")
    void setPlaying(@Param("trackId") String trackId);

    @Modifying
    @Query("UPDATE Track t SET t.isPlaying = false WHERE t.id = :trackId")
    void stopPlaying(@Param("trackId") String trackId);

    List<Track> findByAlbum_IdAndStatusAndTrackOrderGreaterThanOrderByTrackOrderAsc(
            String albumId, Status status, Integer trackOrder);

    List<Track> findByAlbum_IdAndStatusOrderByTrackOrderAsc(String albumId, Status status);
    List<Track> findByStatus(Status status);

    // 1) Exact
    List<Track> findByStatusAndEntityTypeAndNameIgnoreCase(
            Status status, EntityType entityType, String name);

    // 2) Prefix
    List<Track> findByStatusAndEntityTypeAndNameStartingWithIgnoreCaseOrderByPlayCountDesc(
            Status status, EntityType entityType, String prefix, Pageable pageable);

    // 3) Substring
    List<Track> findByStatusAndEntityTypeAndNameContainingIgnoreCaseOrderByPlayCountDesc(
            Status status, EntityType entityType, String infix, Pageable pageable);

    // 4) FULLTEXT (optional, yêu cầu FT index)
    @Query(value = """
      SELECT * , MATCH(name) AGAINST (:q IN NATURAL LANGUAGE MODE) AS score
      FROM track
      WHERE status='ACTIVE' AND entity_type='SONGS'
        AND MATCH(name) AGAINST (:q IN NATURAL LANGUAGE MODE)
      ORDER BY score DESC
      LIMIT :limit
      """, nativeQuery = true)
    List<Track> fulltextByName(@Param("q") String q, @Param("limit") int limit);

    @Query(value = """
      SELECT *, MATCH(name) AGAINST (:q IN BOOLEAN MODE) AS score
      FROM track
      WHERE status='PUBLISHED' AND entity_type='SONGS'
        AND MATCH(name) AGAINST (:q IN BOOLEAN MODE)
      ORDER BY score DESC
      LIMIT :limit
    """, nativeQuery = true)
    List<Track> fulltextBoolean(@Param("q") String q, @Param("limit") int limit);

}
