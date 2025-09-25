package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.PlaylistRequest;

import com.example.nextune_backend.dto.response.PlaylistResponse;
import com.example.nextune_backend.dto.response.TrackCollectionResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.*;
import com.example.nextune_backend.entity.enums.Status;
import com.example.nextune_backend.mapper.PlaylistMapper;
import com.example.nextune_backend.repository.PlaylistRepository;
import com.example.nextune_backend.repository.PlaylistSaveRepository;
import com.example.nextune_backend.repository.TrackCollectionRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.PlaylistService;
import com.example.nextune_backend.utility.UserUtility;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserUtility userUtility;
    private final UserRepository userRepository;
    private final PlaylistMapper playlistMapper;

    private static final int DEFAULT_RECOVER_DAYS = 2;
    private final TrackCollectionRepository trackCollectionRepository;
    private final PlaylistSaveRepository playlistSaveRepository;

    @Override
    @Transactional
    @Caching(evict = { @CacheEvict(cacheNames = "playlists_search", allEntries = true) })
    public PlaylistResponse createPlaylist(PlaylistRequest request) {
        User user = userRepository.findById(userUtility.getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String name = request.getName();
        if (name == null || name.trim().isEmpty()) {
            int count = playlistRepository.countByUserAndStatus(user, Status.PUBLISHED);
            name = "My Playlist #" + (count + 1);
        }
        Playlist playlist = playlistMapper.toEntity(request, user);
        playlist.setName(name);
        playlist.setCreatedAt(LocalDateTime.now());
        playlist.setTotalTracks(0);
        playlist.setTotalTracks(0);
        playlist.setTotalFollowers(0);
        playlist.setTotalDuration(0);
        return playlistMapper.toResponse(playlistRepository.save(playlist));
    }

    @Override
    @Transactional
    @Caching(evict = { @CacheEvict(cacheNames = "playlists_search", allEntries = true) })
    public PlaylistResponse updatePlaylist(String id, PlaylistRequest request) {
        Playlist playlist = getEntityById(id);
        playlist.setUpdatedAt(LocalDateTime.now());
        playlistMapper.updateEntity(request, playlist);
        return playlistMapper.toResponse(playlistRepository.save(playlist));
    }

    @Override
    @Transactional
    @Caching(evict = { @CacheEvict(cacheNames = "playlists_search", allEntries = true) })
    public void deletePlaylist(String id) {
        Playlist playlist = getEntityById(id);
        playlist.setStatus(Status.DELETED);
        playlist.setDeletedAt(LocalDateTime.now());
        playlist.setUpdatedAt(LocalDateTime.now());
        playlistRepository.save(playlist);
    }

    @Override
    public PlaylistResponse getPlaylistById(String id) {
        return playlistMapper.toResponse(getEntityById(id));
    }

    @Override
    public PlaylistResponse getPlaylistByIdForAdmin(String id) {
        return playlistMapper.toResponse(getEntityByIdForAdmin(id));
    }

    @Override
    public List<PlaylistResponse> getAllPlaylists() {
        return playlistMapper.toResponseList(playlistRepository.findAllByStatus(Status.PUBLISHED));
    }

    private Playlist getEntityById(String id) {
        return playlistRepository.findByIdAndStatus(id, Status.PUBLISHED)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
    }

    private Playlist getEntityByIdForAdmin(String id) {
        return playlistRepository.findByIdAndStatusNot(id, Status.DELETED)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));
    }

    @Override
    public List<PlaylistResponse> getAllPlaylistsForAdmin() {
        return playlistMapper.toResponseList(playlistRepository.findAllByStatusNot(Status.DELETED));
    }

    @Override
    public List<PlaylistResponse> getMyPlaylists() {
        User user = userRepository.findById(userUtility.getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Playlist> playlists = playlistRepository.findAllByUserAndStatusNot(user, Status.DELETED);

        return playlistMapper.toResponseList(playlists);
    }


    @Override
    public List<PlaylistResponse> getMyDeletedPlaylistsEligible(Integer days) {
        int d = (days == null || days <= 0) ? DEFAULT_RECOVER_DAYS : days;
        String currentUserId = userUtility.getCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime cutoff = LocalDateTime.now().minusDays(d);
        List<Playlist> deleted = playlistRepository
                .findAllByUserAndStatusAndDeletedAtAfter(user, Status.DELETED, cutoff);

        return playlistMapper.toResponseList(deleted);
    }

    @Override
    public PlaylistResponse recoverMyPlaylist(String id) {
        String currentUserId = userUtility.getCurrentUserId();
        Playlist playlist = playlistRepository
                .findByIdAndUser_IdAndStatus(id, currentUserId, Status.DELETED)
                .orElseThrow(() -> new RuntimeException("Playlist not found or not deleted"));

        // kiểm tra hạn 2 ngày
        LocalDateTime deletedAt = playlist.getDeletedAt();
        if (deletedAt == null || deletedAt.isBefore(LocalDateTime.now().minusDays(DEFAULT_RECOVER_DAYS))) {
            throw new RuntimeException("Playlist cannot be recovered (over 2 days)");
        }

        // Khôi phục: set về UNPUBLISHED (an toàn), clear deletedAt
        playlist.setStatus(Status.UNPUBLISHED);
        playlist.setDeletedAt(null);
        playlist.setUpdatedAt(LocalDateTime.now());

        return playlistMapper.toResponse(playlistRepository.save(playlist));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "playlists_search",
            key = "T(java.util.Objects).hash(#name,#genre,#sortBy,#order,#limit)")
    public List<PlaylistResponse> searchPlaylists(
            String name,
            String genre,
            String sortBy,
            String order,
            int limit
    ) {
        // map sort field
        String sortField = switch ((sortBy == null ? "" : sortBy).toLowerCase()) {
            case "listencount", "listen_count", "playcount", "play_count" -> "totalListenCount";
            case "createdat", "created_at" -> "createdAt";
            default -> "createdAt";
        };
        Sort.Direction dir = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(0, Math.max(1, limit), Sort.by(dir, sortField));

        Specification<Playlist> spec = Specification.allOf();

        //  chỉ lấy playlist PUBLISHED (và chưa bị xóa mềm nếu bạn dùng deletedAt)
        spec = spec.and((root, cq, cb) -> cb.equal(root.get("status"), Status.PUBLISHED));
        spec = spec.and((root, cq, cb) -> cb.isNull(root.get("deletedAt")));

        // name LIKE
        if (name != null && !name.isBlank()) {
            String like = "%" + name.toLowerCase().trim() + "%";
            spec = spec.and((root, cq, cb) -> cb.like(cb.lower(root.get("name")), like));
        }

        // genre: có ít nhất 1 track thuộc playlist có genre trùng (track_genre) HOẶC album của track có genre trùng (album_genre)
        if (genre != null && !genre.isBlank()) {
            String likeGenre = "%" + genre.toLowerCase().trim() + "%";

            spec = spec.and((root, cq, cb) -> {
                // EXISTS TrackGenre qua PlaylistTrack
                Subquery<TrackGenre> sqTG = cq.subquery(TrackGenre.class);
                Root<TrackGenre> tg = sqTG.from(TrackGenre.class);
                Join<TrackGenre, Genre> g1 = tg.join("genre");
                Root<TrackCollection> pt1 = sqTG.from(TrackCollection.class);
                var t1 = pt1.join("track");
                sqTG.select(tg)
                        .where(
                                cb.equal(tg.get("track"), pt1.get("track")),
                                cb.equal(pt1.get("playlist"), root),
                                cb.equal(t1.get("status"), Status.PUBLISHED),
                                cb.like(cb.lower(g1.get("name")), likeGenre)
                        );

                // EXISTS AlbumGenre qua PlaylistTrack -> Track -> Album
                Subquery<AlbumGenre> sqAG = cq.subquery(AlbumGenre.class);
                Root<AlbumGenre> ag = sqAG.from(AlbumGenre.class);
                Join<AlbumGenre, Genre> g2 = ag.join("genre");
                Root<TrackCollection> pt2 = sqAG.from(TrackCollection.class);
                var t2 = pt2.join("track");
                sqAG.select(ag)
                        .where(
                                cb.equal(ag.get("album"), t2.get("album")),
                                cb.equal(pt2.get("playlist"), root),
                                cb.equal(t2.get("status"), Status.PUBLISHED),
                                cb.like(cb.lower(g2.get("name")), likeGenre)
                        );

                return cb.or(cb.exists(sqTG), cb.exists(sqAG));
            });
        }

        Page<Playlist> page = playlistRepository.findAll(spec, pageable);
        return playlistMapper.toResponseList(page.getContent());

    }




}
