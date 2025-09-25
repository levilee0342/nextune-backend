package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.AlbumRequest;
import com.example.nextune_backend.dto.response.AlbumResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.*;
import com.example.nextune_backend.entity.enums.EntityType;
import com.example.nextune_backend.entity.enums.Status;
import com.example.nextune_backend.mapper.AlbumMapper;
import com.example.nextune_backend.mapper.TrackMapper;
import com.example.nextune_backend.mapper.TrackMapper;
import com.example.nextune_backend.repository.AlbumRepository;
import com.example.nextune_backend.repository.TrackRepository;
import com.example.nextune_backend.repository.TrackRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.repository.TrackRepository;
import com.example.nextune_backend.service.AlbumService;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final AlbumMapper albumMapper;
    private final TrackRepository trackRepository;
    private final TrackMapper trackMapper;


    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "albums_search", allEntries = true)
            }
    )
    public AlbumResponse createAlbum(AlbumRequest request) {
        User artist = userRepository.findById(request.getArtistId())
                .orElseThrow(() -> new RuntimeException("Artist not found"));

        Album album = albumMapper.map(request);
        album.setArtist(artist);
        album.setTotalDuration(0);
        album.setTotalSaves(0);
        album.setTotalSong(0);
        albumRepository.save(album);

        AlbumResponse albumResponse = albumMapper.map(album);
        albumResponse.setArtistId(artist.getId());
        return albumResponse;
    }

    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "albums_search", allEntries = true)
            }
    )
    public AlbumResponse updateAlbum(String id, AlbumRequest updatedAlbum) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        // Dùng mapper để copy các field không null
        albumMapper.updateAlbumFromRequest(updatedAlbum, album);
       // Nếu muốn update artist theo artistId mới
        if (updatedAlbum.getArtistId() != null) {
            User artist = userRepository.findById(updatedAlbum.getArtistId())
                    .orElseThrow(() -> new RuntimeException("Artist not found"));
            album.setArtist(artist);
        }

        albumRepository.save(album);

        AlbumResponse albumResponse = albumMapper.map(album);
        albumResponse.setArtistId(album.getArtist().getId());
        return albumMapper.map(album);
    }


    @Override
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(cacheNames = "albums_search", allEntries = true)
            }
    )
    public void deleteAlbum(String id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));
        album.setStatus(Status.DELETED);
        albumRepository.save(album);
    }

    @Override
    public AlbumResponse getAlbumById(String id) {
        Album album = albumRepository.findByIdAndStatus(id, Status.PUBLISHED)
                .orElseThrow(() -> new RuntimeException("Album not found or deleted"));
        return albumMapper.map(album);
    }

    @Override
    public List<AlbumResponse> getAllAlbums() {
        return albumMapper.map(albumRepository.findAllByStatus(Status.PUBLISHED));
    }

    @Override
    public List<AlbumResponse> getAlbumsByArtist(String artistId) {
        return albumMapper.map(albumRepository.findByArtist_IdAndStatus(artistId, Status.PUBLISHED));
    }

    @Override
    public AlbumResponse getAlbumByIdForAdmin(String id) {
        Album album = albumRepository.findByIdAndStatusNot(id, Status.DELETED)
                .orElseThrow(() -> new RuntimeException("Album not found or deleted"));
        return albumMapper.map(album);
    }

    @Override
    public List<AlbumResponse> getAllAlbumsForAdmin() {
        return albumMapper.map(albumRepository.findAllByStatusNot(Status.DELETED));
    }

    @Override
    public List<AlbumResponse> getAlbumsByArtistForAdmin(String artistId) {
        return albumMapper.map(albumRepository.findByArtist_IdAndStatusNot(artistId, Status.DELETED));
    }

    @Transactional
    public String addTrackToAlbum(String albumId, String trackId, Integer trackOrder) {
        try {
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("Album not found"));

            Track track = trackRepository.findById(trackId)
                    .orElseThrow(() -> new RuntimeException("Track not found"));

            // Gán album cho track
            track.setAlbum(album);
            if (trackOrder != null) {
                track.setTrackOrder(trackOrder);
            }
            trackRepository.save(track);

            // Update aggregate
            album.setTotalSong(album.getTotalSong() + 1);
            album.setTotalDuration(album.getTotalDuration() + track.getDuration());

            albumRepository.save(album);

            return "Track added to album successfully";
        } catch (Exception e) {
            return "Add Failed: " + e.getMessage();
        }
    }

    @Transactional
    public String removeTrackFromAlbum(String albumId, String trackId) {
        try {
            Album album = albumRepository.findById(albumId)
                    .orElseThrow(() -> new RuntimeException("Album not found"));

            Track track = trackRepository.findById(trackId)
                    .orElseThrow(() -> new RuntimeException("Track not found"));

            if (track.getAlbum() == null || !track.getAlbum().getId().equals(albumId)) {
                throw new RuntimeException("Track does not belong to this album");
            }

            // Xoá album khỏi track
            track.setAlbum(null);
            track.setTrackOrder(null);
            trackRepository.save(track);

            // Update aggregate
            album.setTotalSong(Math.max(0, album.getTotalSong() - 1));
            album.setTotalDuration(Math.max(0, album.getTotalDuration() - track.getDuration()));

            albumRepository.save(album);

            return "Track removed from album successfully";
        } catch (Exception e) {
            return "Remove Failed: " + e.getMessage();
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "albums_search",
            key = "T(java.util.Objects).hash(#name,#genre,#entityType,#artistId,#sortBy,#order,#limit)")
    public List<AlbumResponse> searchAlbums(
            String name,
            String genre,
            EntityType entityType,
            String artistId,
            String sortBy,
            String order,
            int limit
    ) {
        // map sortBy: listenCount -> totalListenCount; createdAt -> releaseDate
        String sortField = switch ((sortBy == null ? "" : sortBy).toLowerCase()) {
            case "listencount", "listen_count", "playcount", "play_count" -> "totalListenCount";
            case "createdat", "created_at", "releasedate", "release_date" -> "releaseDate";
            default -> "releaseDate";
        };
        Sort.Direction dir = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(0, Math.max(1, limit), Sort.by(dir, sortField));

        Specification<Album> spec = Specification.allOf();

        // chỉ lấy album PUBLISHED
        spec = spec.and((root, cq, cb) -> cb.equal(root.get("status"), Status.PUBLISHED));

        // name LIKE (album.name)
        if (name != null && !name.isBlank()) {
            String like = "%" + name.toLowerCase().trim() + "%";
            spec = spec.and((root, cq, cb) -> cb.like(cb.lower(root.get("name")), like));
        }

        // entityType SONG/PODCAST
        if (entityType != null) {
            spec = spec.and((root, cq, cb) -> cb.equal(root.get("entityType"), entityType));
        }

        // artist
        if (artistId != null && !artistId.isBlank()) {
            spec = spec.and((root, cq, cb) -> {
                // album.artist.id = :artistId
                var artistJoin = root.join("artist", JoinType.LEFT);
                var byAlbumArtist = cb.equal(artistJoin.get("id"), artistId);

                // EXISTS in TrackArtist: track thuộc album này, track PUBLISHED và track_artist.artist_id = :artistId
                Subquery<TrackArtist> sqTA = cq.subquery(TrackArtist.class);
                Root<TrackArtist> ta = sqTA.from(TrackArtist.class);
                var t = ta.join("track");
                sqTA.select(ta).where(
                        cb.equal(ta.get("artist").get("id"), artistId),
                        cb.equal(t.get("album"), root),
                        cb.equal(t.get("status"), Status.PUBLISHED)
                );

                return cb.or(byAlbumArtist, cb.exists(sqTA));
            });
        }

        // genre theo album_genre hoặc (tuỳ chọn) track_genre
        if (genre != null && !genre.isBlank()) {
            String likeGenre = "%" + genre.toLowerCase().trim() + "%";

            spec = spec.and((root, cq, cb) -> {
                // EXISTS (SELECT 1 FROM album_genre ag JOIN genre g ON ... WHERE ag.album = root AND lower(g.name) LIKE :likeGenre)
                Subquery<AlbumGenre> sqAlbumGenre = cq.subquery(AlbumGenre.class);
                Root<AlbumGenre> ag = sqAlbumGenre.from(AlbumGenre.class);
                var g = ag.join("genre");
                sqAlbumGenre.select(ag)
                        .where(
                                cb.equal(ag.get("album"), root),
                                cb.like(cb.lower(g.get("name")), likeGenre)
                        );

                Subquery<TrackGenre> sqTrackGenre = cq.subquery(TrackGenre.class);
                Root<TrackGenre> tg = sqTrackGenre.from(TrackGenre.class);
                var g2 = tg.join("genre");
                var t  = tg.join("track");
                sqTrackGenre.select(tg)
                        .where(
                                cb.equal(t.get("album"), root),
                                cb.equal(t.get("status"), Status.PUBLISHED),
                                cb.like(cb.lower(g2.get("name")), likeGenre)
                        );

                return cb.or(cb.exists(sqAlbumGenre), cb.exists(sqTrackGenre));
            });
        }

        Page<Album> page = albumRepository.findAll(spec, pageable);
        return albumMapper.map(page.getContent());
    }

    private Sort buildTrackSort(String sortBy, String order) {
        // map sortBy: name | publishedAt | duration | playCount | trackOrder
        String field = switch ((sortBy == null ? "" : sortBy).toLowerCase()) {
            case "name" -> "name";
            case "publishedat", "published_at" -> "publishedAt";
            case "duration" -> "duration";
            case "playcount", "play_count", "listencount", "listen_count" -> "playCount";
            case "trackorder", "track_order", "order" -> "trackOrder";
            default -> "trackOrder";
        };
        Sort.Direction dir = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;

        // mặc định ưu tiên trackOrder rồi publishedAt DESC để ổn định trình tự
        if (field.equals("trackOrder")) {
            return Sort.by(Sort.Order.asc("trackOrder"), Sort.Order.desc("publishedAt"));
        }
        return Sort.by(new Sort.Order(dir, field), Sort.Order.asc("trackOrder"), Sort.Order.desc("publishedAt"));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "album_tracks",
            key = "T(java.util.Objects).hash(#albumId,#sortBy,#order)")
    public List<TrackResponse> getAlbumTracks(String albumId, String sortBy, String order) {
        // đảm bảo album tồn tại & không bị xóa
        albumRepository.findByIdAndStatus(albumId, Status.PUBLISHED)
                .orElseThrow(() -> new RuntimeException("Album not found or deleted"));

        Sort sort = buildTrackSort(sortBy, order);
        List<Track> tracks = trackRepository.findByAlbum_IdAndStatus(albumId, Status.PUBLISHED, sort);
        // Chỉ trả track PUBLISHED (tránh nhầm DELETED/DRAFT)
        tracks.removeIf(t -> t.getStatus() != Status.PUBLISHED);

        return trackMapper.map(tracks);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "album_tracks_page",
            key = "T(java.util.Objects).hash(#albumId,#sortBy,#order,#page,#size)")
    public Page<TrackResponse> getAlbumTracksPage(String albumId, String sortBy, String order, int page, int size) {
        albumRepository.findByIdAndStatus(albumId, Status.PUBLISHED)
                .orElseThrow(() -> new RuntimeException("Album not found or deleted"));

        Sort sort = buildTrackSort(sortBy, order);
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

        Page<Track> p = trackRepository.findByAlbum_IdAndStatus(albumId, Status.PUBLISHED, pageable);
        List<TrackResponse> dto = trackMapper.map(p.getContent());

        return new PageImpl<>(dto, pageable, p.getTotalElements());
    }
}
