package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.TrackQueue;
import com.example.nextune_backend.dto.request.TrackPlayRequest;
import com.example.nextune_backend.dto.request.TrackRequest;
import com.example.nextune_backend.dto.response.AlbumResponse;
import com.example.nextune_backend.dto.response.PlaylistResponse;
import com.example.nextune_backend.dto.response.TrackPlayResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.*;
import com.example.nextune_backend.entity.enums.EntityType;
import com.example.nextune_backend.entity.enums.Status;
import com.example.nextune_backend.logging.HttpLoggingFilter;
import com.example.nextune_backend.mapper.AlbumMapper;
import com.example.nextune_backend.mapper.PlaylistMapper;
import com.example.nextune_backend.mapper.TrackMapper;
import com.example.nextune_backend.repository.AlbumRepository;
import com.example.nextune_backend.repository.PlaylistRepository;
import com.example.nextune_backend.repository.TrackCollectionRepository;
import com.example.nextune_backend.repository.TrackRepository;
import com.example.nextune_backend.service.*;
import com.example.nextune_backend.utility.UserUtility;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.cache.annotation.*;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrackServiceImpl implements TrackService {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final TrackMapper trackMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final AlbumMapper albumMapper;
    private final PlaylistMapper playlistMapper;
    private final PlaylistRepository playlistRepository;
    private final AlbumService albumService;
    private final PlaylistService playlistService;
    private final TrackCollectionRepository trackCollectionRepository;
    private final SimilarService similarService;
    private final PlayerQueueService playerQueueService;
    private final UserUtility userUtility;
    private final HttpLoggingFilter httpLoggingFilter;

    private String playCountKey(String trackId) {
        return "track:" + trackId + ":playCount";
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "tracks_all", allEntries = true),
            @CacheEvict(cacheNames = "tracks_by_album", allEntries = true),
            @CacheEvict(cacheNames = "tracks_search", allEntries = true)
    })
    public TrackResponse createTrack(TrackRequest request) {
        Album album = null;

        if (request.getAlbumId() != null) {
            album = getAlbum(request.getAlbumId());
            album.setTotalSong(album.getTotalSong() + 1);
            album.setTotalDuration(album.getTotalDuration() + request.getDuration());
        }

        Track track = trackMapper.map(request, album);

        if (album != null) {
            track.setTrackOrder(album.getTotalSong());
        } else {
            track.setTrackOrder(0);
        }

        track.setExplicit(Boolean.TRUE.equals(request.getExplicit()));
        track.setPlayCount(0L);

        System.out.println(track);

        trackRepository.save(track);

        stringRedisTemplate.opsForValue()
                .setIfAbsent(playCountKey(track.getId()), "0", 7, TimeUnit.DAYS);

        return trackMapper.map(track);
    }

    @Transactional
    @Override
    @Caching(
            put = { @CachePut(cacheNames = "track", key = "#id") },
            evict = {
                    @CacheEvict(cacheNames = "tracks_all", allEntries = true),
                    @CacheEvict(cacheNames = "tracks_by_album", allEntries = true),
                    @CacheEvict(cacheNames = "tracks_search", allEntries = true)
            }
    )
    public TrackResponse updateTrack(String id, TrackRequest request) {
        Track track = getTrackByIdForAdmin(id);

        if (request.getAlbumId() != null) {
            Album album = getAlbum(request.getAlbumId());
            track.setAlbum(album);
        }

        trackMapper.updateTrackFromRequest(request, track);
        trackRepository.save(track);
        return trackMapper.map(track);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "track", key = "#id"),
            @CacheEvict(cacheNames = "tracks_all", allEntries = true),
            @CacheEvict(cacheNames = "tracks_by_album", allEntries = true),
            @CacheEvict(cacheNames = "tracks_search", allEntries = true)
    })
    public void deleteTrack(String id) {
        Track track = getTrackByIdForAdmin(id);
        track.setStatus(Status.DELETED);
        trackRepository.save(track);

        stringRedisTemplate.delete(playCountKey(id));
    }

    @Override
    @Cacheable(cacheNames = "track", key = "#id")
    public TrackResponse getTrackByIdResponse(String id) {
        return trackMapper.map(getTrackById(id));
    }

    @Override
    @Cacheable(cacheNames = "tracks_all")
    public List<TrackResponse> getAllTracks() {
        return trackMapper.map(trackRepository.findAllByStatus(Status.PUBLISHED));
    }

    @Override
    @Cacheable(cacheNames = "tracks_by_album", key = "#albumId")
    public List<TrackResponse> getTracksByAlbum(String albumId) {
        return trackMapper.map(trackRepository.findByAlbum_IdAndStatus(albumId, Status.PUBLISHED));
    }

    @Override
    public TrackResponse getTrackByIdResponseForAdmin(String id) {
        return trackMapper.map(getTrackByIdForAdmin(id));
    }

    @Override
    public List<TrackResponse> getAllTracksForAdmin() {
        return trackMapper.map(trackRepository.findAllByStatusNot(Status.DELETED));
    }

    @Override
    public List<TrackResponse> getTracksByAlbumForAdmin(String albumId) {
        return trackMapper.map(trackRepository.findByAlbum_IdAndStatus(albumId, Status.PUBLISHED));
    }


    @Transactional
    @Override
    @Caching(
            put = { @CachePut(cacheNames = "track", key = "#trackId") },
            evict = {
                    @CacheEvict(cacheNames = "tracks_all", allEntries = true),
                    @CacheEvict(cacheNames = "tracks_by_album", allEntries = true)
            }
    )
    public TrackResponse updateStatus(String trackId, String statusStr) {
        Track track = getTrackByIdForAdmin(trackId);
        track.setStatus(Status.valueOf(statusStr.toUpperCase()));
        trackRepository.save(track);
        return trackMapper.map(track);
    }

    @Transactional
    @Override
    @CachePut(cacheNames = "track", key = "#trackId")
    public TrackResponse incrementPlayCount(String trackId) {
        // đảm bảo counter tồn tại
        String key = playCountKey(trackId);
        Track track = getTrackById(trackId);


        if (Boolean.FALSE.equals(stringRedisTemplate.hasKey(key))) {
            stringRedisTemplate.opsForValue().set(key, String.valueOf(track.getPlayCount()), 7, TimeUnit.DAYS);
        }

        Long newCount = stringRedisTemplate.opsForValue().increment(key);
        track.setPlayCount(newCount != null ? newCount : track.getPlayCount() + 1);
        trackRepository.save(track);

        return trackMapper.map(track);
    }


    private Track getTrackById(String id) {
        return trackRepository.findByIdAndStatus(id, Status.PUBLISHED)
                .orElseThrow(() -> new RuntimeException("Track not found or not published"));
    }

    private Track getTrackByIdForAdmin(String id) {
        return trackRepository.findByIdAndStatusNot(id, Status.DELETED)
                .orElseThrow(() -> new RuntimeException("Track not found or not published"));
    }

    @Override
    @Transactional
    public TrackPlayResponse playSong(String songId, TrackPlayRequest request) {
        // Chuẩn hóa request để không bị NPE
        if (request == null) {
            request = new TrackPlayRequest();  // hoặc Optional.ofNullable(...)
        }

        // 0) tăng playCount & lấy track
        TrackResponse trackResponse = incrementPlayCount(songId);
        Track track = trackRepository.findById(songId)
                .orElseThrow(() -> new EntityNotFoundException("Track not found with id: " + songId));

        // helper
        Predicate<String> hasText = s -> s != null && !s.isBlank()
                && !"string".equalsIgnoreCase(s) && !"null".equalsIgnoreCase(s);

        // 1) Build queueIds & currentIndex
        List<String> queueIds = new ArrayList<>();
        int currentIndex = 0;

        String source = "SIMILAR";
        String sourceId = songId;

        if (hasText.test(request.getAlbumId()) && albumRepository.existsById(request.getAlbumId())) {
            source = "ALBUM";
            sourceId = request.getAlbumId();

            // Lấy toàn bộ trackIds của album theo trackOrder ↑
            List<Track> albumTracks = trackRepository.findByAlbum_IdAndStatusOrderByTrackOrderAsc(
                    request.getAlbumId(), Status.PUBLISHED);
            List<String> ordered = albumTracks.stream().map(Track::getId).toList();

            if (ordered.contains(songId)) {
                // Queue bắt đầu từ bài đầu tiên của album, nhưng currentIndex = vị trí của bài đang chọn
                queueIds.addAll(ordered);
                currentIndex = ordered.indexOf(songId);
            } else {
                // Bài chọn không thuộc album -> chèn bài chọn lên đầu, rồi đến full album (de-dup)
                queueIds.add(songId);
                for (String id : ordered) if (!id.equals(songId)) queueIds.add(id);
                currentIndex = 0; // bắt đầu nghe ở bài đã chọn
            }

        } else if (hasText.test(request.getPlaylistId()) && playlistRepository.existsById(request.getPlaylistId())) {
            source = "PLAYLIST";
            sourceId = request.getPlaylistId();

            // Lấy toàn bộ trackIds của playlist theo thứ tự track_collection.order
            List<String> ordered = trackCollectionRepository.findAllTrackIdsInPlaylistOrdered(request.getPlaylistId());

            if (ordered.contains(songId)) {
                queueIds.addAll(ordered);
                currentIndex = ordered.indexOf(songId);
            } else {
                // Bài chọn không thuộc playlist -> chèn bài chọn lên đầu, rồi đến full playlist (de-dup)
                queueIds.add(songId);
                for (String id : ordered) if (!id.equals(songId)) queueIds.add(id);
                currentIndex = 0;
            }

        } else {
            // SIMILAR giữ nguyên
            queueIds.add(songId);
            List<String> simIds = similarService.getSimilarTrackIds(track.getEntityType(), songId, 50);
            queueIds.addAll(simIds);
            currentIndex = 0;
        }

        // 2) Lưu queue theo user/session
        String ownerId = userUtility.getCurrentUserId();
        TrackQueue q = TrackQueue.builder()
                .trackIds(queueIds)
                .currentIndex(currentIndex)        //  bắt đầu nghe ở bài đã chọn
                .source(source)
                .sourceId(sourceId)
                .build();
        playerQueueService.saveQueue(ownerId, q);

        // 3) album/playlist response
        AlbumResponse albumResponse = null;
        if (hasText.test(request.getAlbumId())) {
            try { albumResponse = albumService.getAlbumById(request.getAlbumId()); } catch (Exception ignored) {}
        }
        PlaylistResponse playlistResponse = null;
        if (hasText.test(request.getPlaylistId())) {
            try { playlistResponse = playlistService.getPlaylistById(request.getPlaylistId()); } catch (Exception ignored) {}
        }

        // 4) trả về kèm queue
        return TrackPlayResponse.builder()
                .trackUrl(trackResponse.getTrackUrl())
                .album(albumResponse)
                .playlist(playlistResponse)
                .track(trackResponse)
                .queueIds(queueIds)
                .currentIndex(currentIndex)
                .source(source)
                .sourceId(sourceId)
                .build();

    }


    @Override
    @Transactional
    public TrackPlayResponse stopSong(String songId, TrackPlayRequest request) {
        // Tắt bài hát hiện tại
        trackRepository.stopPlaying(songId);

        AlbumResponse albumResponse = null;
        if (request.getAlbumId() != null && !request.getAlbumId().isBlank()) {
            try {
                albumResponse = albumService.getAlbumById(request.getAlbumId());
            } catch (Exception e) {
                albumResponse = null;
            }
        }

        PlaylistResponse playlistResponse = null;
        if (request.getPlaylistId() != null && !request.getPlaylistId().isBlank()) {
            try {
                playlistResponse = playlistService.getPlaylistById(request.getPlaylistId());
            } catch (Exception e) {
                playlistResponse = null;
            }
        }

        Track track = trackRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Track not found: " + songId));

        TrackResponse trackResponse = trackMapper.map(track);

        return TrackPlayResponse.builder()
                .trackUrl(trackResponse.getTrackUrl())
                .album(albumResponse)
                .playlist(playlistResponse)
                .track(trackResponse)
                .build();
    }





    private Album getAlbum(String albumId) {
        return albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Album not found"));
    }

    @Override
    @Cacheable(cacheNames = "tracks_search",
            key = "T(java.util.Objects).hash(#name,#genre,#entityType,#artistId,#sortBy,#order,#limit)")
    public List<TrackResponse> searchTracks(
            String name,
            String genre,
            EntityType entityType,
            String artistId,
            String sortBy,
            String order,
            int limit
    ) {
        // Chuẩn hóa sort field
        String sortField = switch ((sortBy == null ? "" : sortBy).toLowerCase()) {
            case "listencount", "listen_count", "playcount", "play_count" -> "playCount";
            case "createdat", "created_at", "publishedat", "published_at" -> "publishedAt";
            default -> "publishedAt";
        };
        Sort.Direction dir = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(0, Math.max(1, limit), Sort.by(dir, sortField));

        Specification<Track> spec = Specification.allOf();
        spec = spec.and((root, cq, cb) -> cb.equal(root.get("status"), Status.PUBLISHED));

        // name LIKE
        if (name != null && !name.isBlank()) {
            String like = "%" + name.toLowerCase().trim() + "%";
            spec = spec.and((root, cq, cb) -> cb.like(cb.lower(root.get("name")), like));
        }

        // entityType
        if (entityType != null) {
            spec = spec.and((root, cq, cb) -> cb.equal(root.get("entityType"), entityType));
        }

        // artist
        if (artistId != null && !artistId.isBlank()) {
            spec = spec.and((root, cq, cb) -> {
                // join album -> artist
                var albumJoin = root.join("album", JoinType.LEFT);
                var artistJoin = albumJoin.join("artist", JoinType.LEFT);
                var byAlbumArtist = cb.equal(artistJoin.get("id"), artistId);

                // exists in TrackArtist
                Subquery<TrackArtist> sqTA = cq.subquery(TrackArtist.class);
                Root<TrackArtist> ta = sqTA.from(TrackArtist.class);
                sqTA.select(ta)
                        .where(
                                cb.equal(ta.get("track"), root),
                                cb.equal(ta.get("artist").get("id"), artistId)
                        );

                return cb.or(byAlbumArtist, cb.exists(sqTA));
            });
        }

        // genre theo TrackGenre.name hoặc AlbumGenre.name (subquery, không cần mapped collection)
        if (genre != null && !genre.isBlank()) {
            String likeGenre = "%" + genre.toLowerCase().trim() + "%";

            spec = spec.and((root, cq, cb) -> {
                // EXISTS (SELECT 1 FROM TrackGenre tg JOIN tg.genre g WHERE tg.track = root AND lower(g.name) LIKE :likeGenre)
                Subquery<TrackGenre> sqTrackGenre = cq.subquery(TrackGenre.class);
                Root<TrackGenre> tg = sqTrackGenre.from(TrackGenre.class);
                Join<TrackGenre, Genre> g1 = tg.join("genre");
                sqTrackGenre.select(tg)
                        .where(
                                cb.equal(tg.get("track"), root),
                                cb.like(cb.lower(g1.get("name")), likeGenre)
                        );

                // EXISTS (SELECT 1 FROM AlbumGenre ag JOIN ag.genre g WHERE ag.album = root.album AND lower(g.name) LIKE :likeGenre)
                Subquery<AlbumGenre> sqAlbumGenre = cq.subquery(AlbumGenre.class);
                Root<AlbumGenre> ag = sqAlbumGenre.from(AlbumGenre.class);
                Join<AlbumGenre, Genre> g2 = ag.join("genre");
                sqAlbumGenre.select(ag)
                        .where(
                                cb.equal(ag.get("album"), root.get("album")),
                                cb.like(cb.lower(g2.get("name")), likeGenre)
                        );

                // thỏa ít nhất 1 trong 2
                return cb.or(
                        cb.exists(sqTrackGenre),
                        cb.exists(sqAlbumGenre)
                );
            });
        }

        Page<Track> page = trackRepository.findAll(spec, pageable);
        return trackMapper.map(page.getContent());
    }

    @Override
    public List<TrackResponse> getRandomTracks(EntityType entityType, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 50); // 1..50
        String typeStr = (entityType == null) ? null : entityType.name(); // SONG | PODCAST
        List<Track> picks = trackRepository.findRandomPublished(typeStr, safeLimit);
        return trackMapper.map(picks);
    }

    @Override
    @Cacheable(cacheNames = "tracks", key = "#ids")
    public List<TrackResponse> getTracksByIds(List<String> ids) {
        return ids.stream()
                .map(this::getTrackByIdResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void publishScheduledTracks() {
        List<Track> pendingTracks = trackRepository.findByStatus(Status.ACTIVE);
        LocalDateTime now = LocalDateTime.now();

        System.out.println("🔍 Found {} pending tracks at {}"+ pendingTracks.size() + now);

        for (Track track : pendingTracks) {
            if (track.getPublishedAt() != null && !track.getPublishedAt().isAfter(now)) {

                track.setStatus(Status.PUBLISHED);
                trackRepository.save(track);
            } else {
                System.out.println("⏳ Track [{} - {}] is not yet ready (publishedAt = {})"+
                        track.getId()+ track.getName()+ track.getPublishedAt());
            }
        }
    }


}
