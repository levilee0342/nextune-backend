package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.TrackCollection;
import com.example.nextune_backend.entity.TrackCollectionId;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrackCollectionRepository extends JpaRepository<TrackCollection, TrackCollectionId> {
    List<TrackCollection> findByPlaylist_Id(String playlistId);
    @Modifying
    @Query("DELETE FROM TrackCollection tc WHERE tc.track.id = :trackId AND tc.playlist.id IN :playlistIds")
    void deleteTrackFromPlaylists(@Param("trackId") String trackId, @Param("playlistIds") List<String> playlistIds);

    @Modifying
    @Transactional
    @Query(
            value = "INSERT INTO track_collection (track_id, playlist_id, track_order) " +
                    "VALUES (:trackId, :playlistId, :trackOrder) " +
                    "ON DUPLICATE KEY UPDATE track_id = track_id",
            nativeQuery = true
    )
    void insertTrackIntoPlaylist(@Param("trackId") String trackId,
                                 @Param("playlistId") String playlistId,
                                 @Param("trackOrder") Integer trackOrder);

    @Query("SELECT tc.track FROM TrackCollection tc WHERE tc.playlist.id = :playlistId ORDER BY tc.trackOrder ASC")
    List<Track> findTracksByPlaylistId(@Param("playlistId") String playlistId);


    @Query("SELECT COUNT(tc) FROM TrackCollection tc WHERE tc.playlist.id = :playlistId")
    long countTracksByPlaylistId(@Param("playlistId") String playlistId);

    @Query("SELECT SUM(tc.track.duration) FROM TrackCollection tc WHERE tc.playlist.id = :playlistId")
    Long sumDurationByPlaylistId(@Param("playlistId") String playlistId);

    List<TrackCollection> findAllByTrack_Id(String trackId);
    List<TrackCollection> findByTrackId(String trackId);

    // trackOrder của một track trong playlist
    @Query("""
        SELECT tc.trackOrder 
        FROM TrackCollection tc 
        WHERE tc.playlist.id = :playlistId AND tc.track.id = :trackId
    """)
    Optional<Integer> findTrackOrderInPlaylist(@Param("playlistId") String playlistId,
                                               @Param("trackId") String trackId);

    // các track có order > current trong playlist
    @Query("""
        SELECT tc.track.id 
        FROM TrackCollection tc 
        JOIN tc.track t
        WHERE tc.playlist.id = :playlistId
          AND tc.trackOrder > :currentOrder
          AND t.status = 'PUBLISHED'
        ORDER BY tc.trackOrder ASC
    """)
    List<String> findNextTrackIdsInPlaylist(@Param("playlistId") String playlistId,
                                            @Param("currentOrder") Integer currentOrder);

    @Query("""
      SELECT tc.track.id FROM TrackCollection tc 
      JOIN tc.track t
      WHERE tc.playlist.id = :playlistId AND t.status='PUBLISHED'
      ORDER BY tc.trackOrder ASC
    """)
    List<String> findAllTrackIdsInPlaylistOrdered(@Param("playlistId") String playlistId);

    @Query("SELECT tc FROM TrackCollection tc " +
            "JOIN tc.track t " +
            "WHERE tc.playlist.id = :playlistId AND t.status = com.example.nextune_backend.entity.enums.Status.PUBLISHED " +
            "ORDER BY tc.trackOrder ASC")
    List<TrackCollection> findPublishedByPlaylistOrdered(@Param("playlistId") String playlistId);

}
