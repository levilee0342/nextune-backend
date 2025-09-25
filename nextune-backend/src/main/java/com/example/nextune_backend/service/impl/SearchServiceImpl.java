package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.response.*;
import com.example.nextune_backend.entity.*;
import com.example.nextune_backend.entity.enums.EntityType;
import com.example.nextune_backend.entity.enums.RoleName;
import com.example.nextune_backend.entity.enums.Status;
import com.example.nextune_backend.mapper.AlbumMapper;
import com.example.nextune_backend.mapper.PlaylistMapper;
import com.example.nextune_backend.mapper.TrackMapper;
import com.example.nextune_backend.repository.AlbumRepository;
import com.example.nextune_backend.repository.PlaylistRepository;
import com.example.nextune_backend.repository.TrackRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.SearchService;
import com.example.nextune_backend.utility.NormalizerUtility;
import com.example.nextune_backend.utility.SimilarityUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final TrackMapper trackMapper;
    private final AlbumMapper albumMapper;
    private final PlaylistMapper playlistMapper;

    @Override
    @Transactional(readOnly = true)
    public SearchResponse search(String q, String types, int limit) {
        String query = (q == null) ? "" : q.trim();
        boolean all = "all".equalsIgnoreCase(types);
        Set<String> typeSet = Arrays.stream(types.split(","))
                .map(s -> s.trim().toLowerCase())
                .collect(Collectors.toSet());

        // [CHANGED] nới rộng số ứng viên đọc ra để re-rank
        final int k = Math.max(limit * 3, 30); // đọc nhiều hơn rồi re-rank, cắt xuống limit

        Pageable limitByCreated = PageRequest.of(0, Math.max(1, k), Sort.by(Sort.Direction.DESC, "createdAt")); // [CHANGED]
        Pageable limitByPlay    = PageRequest.of(0, Math.max(1, k), Sort.by(Sort.Direction.DESC, "playCount")); // [CHANGED]
        Pageable limitByRelease = PageRequest.of(0, Math.max(1, k), Sort.by(Sort.Direction.DESC, "releaseDate")); // [CHANGED]

        List<TrackResponse> songs = Collections.emptyList();
        List<AlbumResponse> albums = Collections.emptyList();
        List<UserCard> artists = Collections.emptyList();
        List<PlaylistResponse> playlists = Collections.emptyList();
        List<AlbumResponse> podcastShows = Collections.emptyList();
        List<TrackResponse> podcastEpisodes = Collections.emptyList();
        List<UserCard> profiles = Collections.emptyList();

        if (all || typeSet.contains("songs") || typeSet.contains("song")) {
            // [CHANGED] lấy nhiều rồi re-rank
            var page = trackRepository.findAll(songSpec(query), limitByPlay);
            var top = rerankTracks(page.getContent(), query, limit); // [ADDED]
            songs = trackMapper.map(top);
        }
        if (all || typeSet.contains("albums") || typeSet.contains("album")) {
            var page = albumRepository.findAll(albumSpec(query, /*onlyPodcast*/ false), limitByRelease); // [CHANGED]
            // [ADDED] re-rank theo tên (popularity tạm thời 0 nếu không có trường)
            var ranked = rerankByName(page.getContent(), query, limit,
                    Album::getName,
                    a -> {
                        // nếu có trường phổ biến thì đưa vào đây, ví dụ: totalSaves hoặc totalSong
                        // return Optional.ofNullable(a.getTotalSaves()).map(Number::doubleValue).orElse(0.0);
                        return 0.0;
                    });
            albums = albumMapper.map(ranked);
        }
        if (all || typeSet.contains("artists") || typeSet.contains("artist")) {
            var page = userRepository.findAll(artistSpec(query), limitByCreated); // [CHANGED]
            var ranked = rerankByName(page.getContent(), query, limit,
                    User::getName,
                    u -> 0.0 // nếu có followers/popularity -> thay số này
            ); // [ADDED]
            artists = ranked.stream()
                    .map(u -> new UserCard(u.getId(), u.getName(), u.getAvatar()))
                    .toList();
        }
        if (all || typeSet.contains("playlists") || typeSet.contains("playlist")) {
            var page = playlistRepository.findAll(playlistSpec(query), limitByCreated); // [CHANGED]
            var ranked = rerankByName(page.getContent(), query, limit,
                    Playlist::getName,
                    pl -> {
                        // nếu có totalSaves -> dùng làm popularity
                        // return Optional.ofNullable(pl.getTotalSaves()).map(Number::doubleValue).orElse(0.0);
                        return 0.0;
                    }); // [ADDED]
            playlists = playlistMapper.toResponseList(ranked);
        }
        if (all || typeSet.contains("podcasts") || typeSet.contains("podcast")) {
            var shows = albumRepository.findAll(albumSpec(query, /*onlyPodcast*/ true), limitByRelease); // [CHANGED]
            var rankedShows = rerankByName(shows.getContent(), query, limit,
                    Album::getName, a -> 0.0); // [ADDED]
            podcastShows = albumMapper.map(rankedShows);

            var eps = trackRepository.findAll(podcastEpisodeSpec(query), limitByPlay); // [CHANGED]
            // tận dụng rerankTracks cho episode (track)
            var rankedEps = rerankTracks(eps.getContent(), query, limit); // [ADDED]
            podcastEpisodes = trackMapper.map(rankedEps);
        }
        if (all || typeSet.contains("profiles") || typeSet.contains("profile")) {
            var page = userRepository.findAll(profileSpec(query), limitByCreated); // [CHANGED]
            var ranked = rerankByName(page.getContent(), query, limit,
                    User::getName, u -> 0.0); // [ADDED]
            profiles = ranked.stream()
                    .map(u -> new UserCard(u.getId(), u.getName(), u.getAvatar()))
                    .toList();
        }

        SearchResponse res = new SearchResponse();
        res.setQuery(query);
        res.setSongs(songs);
        res.setAlbums(albums);
        res.setArtists(artists);
        res.setPlaylists(playlists);
        res.setPodcastShows(podcastShows);
        res.setPodcastEpisodes(podcastEpisodes);
        res.setProfiles(profiles);
        return res;
    }

    // ---------- SPEC HELPERS ----------

    private Specification<Track> songSpec(String q) {
        return (root, cq, cb) -> {
            Predicate p = cb.conjunction();

            // PUBLISHED
            p = cb.and(p, cb.equal(root.get("status"), Status.PUBLISHED));

            // entityType = SONGS (hoặc null)
            Predicate isSong = cb.or(
                    cb.isNull(root.get("entityType")),
                    cb.equal(cb.upper(root.get("entityType").as(String.class)), "SONGS")
            );
            p = cb.and(p, isSong);

            // [CHANGED] name LIKE + token-LIKE
            Predicate likeName  = likeInsensitive(cb, root.get("name").as(String.class), q);
            Predicate tokenName = tokensOrLikeInsensitive(cb, root.get("name").as(String.class), q); // [ADDED]

            var albumJoin = root.join("album", JoinType.LEFT);
            var albumArtist = albumJoin.join("artist", JoinType.LEFT);

            // [CHANGED] album artist LIKE + token-LIKE
            Predicate likeAlbumArtist  = likeInsensitive(cb, albumArtist.get("name").as(String.class), q);
            Predicate tokenAlbumArtist = tokensOrLikeInsensitive(cb, albumArtist.get("name").as(String.class), q); // [ADDED]

            // exists track_artist artist.name LIKE / token-LIKE
            Subquery<TrackArtist> sqTA = cq.subquery(TrackArtist.class);
            Root<TrackArtist> ta = sqTA.from(TrackArtist.class);
            Predicate likeFeatArtist  = likeInsensitive(cb, ta.get("artist").get("name").as(String.class), q);
            Predicate tokenFeatArtist = tokensOrLikeInsensitive(cb, ta.get("artist").get("name").as(String.class), q); // [ADDED]
            sqTA.select(ta).where(cb.equal(ta.get("track"), root), cb.or(likeFeatArtist, tokenFeatArtist)); // [CHANGED]

            return cb.and(p, cb.or(likeName, tokenName, likeAlbumArtist, tokenAlbumArtist, cb.exists(sqTA))); // [CHANGED]
        };
    }

    private Specification<Album> albumSpec(String q, boolean onlyPodcast) {
        return (root, cq, cb) -> {
            Predicate p = cb.conjunction();
            p = cb.and(p, cb.equal(root.get("status"), Status.PUBLISHED));

            if (onlyPodcast) {
                p = cb.and(p, cb.equal(root.get("entityType"), EntityType.PODCASTS));
            } else {
                p = cb.and(p, cb.or(
                        cb.isNull(root.get("entityType")),
                        cb.notEqual(root.get("entityType"), EntityType.PODCASTS)
                ));
            }

            // [CHANGED] name/artist LIKE + token-LIKE
            Predicate likeName  = likeInsensitive(cb, root.get("name").as(String.class), q);
            Predicate tokenName = tokensOrLikeInsensitive(cb, root.get("name").as(String.class), q); // [ADDED]
            var artist = root.join("artist", JoinType.LEFT);
            Predicate likeArtist  = likeInsensitive(cb, artist.get("name").as(String.class), q);
            Predicate tokenArtist = tokensOrLikeInsensitive(cb, artist.get("name").as(String.class), q); // [ADDED]

            return cb.and(p, cb.or(likeName, tokenName, likeArtist, tokenArtist)); // [CHANGED]
        };
    }

    private Specification<Track> podcastEpisodeSpec(String q) {
        return (root, cq, cb) -> {
            Predicate p = cb.conjunction();
            p = cb.and(p, cb.equal(root.get("status"), Status.PUBLISHED));

            // entityType = PODCASTS (đúng hằng)
            p = cb.and(p, cb.equal(cb.upper(root.get("entityType").as(String.class)), "PODCASTS"));

            // [CHANGED] name/artist LIKE + token-LIKE
            Predicate likeName  = likeInsensitive(cb, root.get("name").as(String.class), q);
            Predicate tokenName = tokensOrLikeInsensitive(cb, root.get("name").as(String.class), q); // [ADDED]
            var albumJoin = root.join("album", JoinType.LEFT);
            var artist = albumJoin.join("artist", JoinType.LEFT);
            Predicate likeArtist  = likeInsensitive(cb, artist.get("name").as(String.class), q);
            Predicate tokenArtist = tokensOrLikeInsensitive(cb, artist.get("name").as(String.class), q); // [ADDED]

            return cb.and(p, cb.or(likeName, tokenName, likeArtist, tokenArtist)); // [CHANGED]
        };
    }

    private Specification<Playlist> playlistSpec(String q) {
        return (root, cq, cb) -> {
            Predicate p = cb.conjunction();

            // chỉ playlist public + published + chưa xóa mềm
            p = cb.and(p, cb.isTrue(root.get("isPublic")));
            p = cb.and(p, cb.equal(root.get("status"), Status.PUBLISHED));
            p = cb.and(p, cb.isNull(root.get("deletedAt")));

            // [CHANGED] name/owner LIKE + token-LIKE
            Predicate likeName  = likeInsensitive(cb, root.get("name").as(String.class), q);
            Predicate tokenName = tokensOrLikeInsensitive(cb, root.get("name").as(String.class), q); // [ADDED]
            var owner = root.join("user", JoinType.LEFT);
            Predicate likeOwner  = likeInsensitive(cb, owner.get("name").as(String.class), q);
            Predicate tokenOwner = tokensOrLikeInsensitive(cb, owner.get("name").as(String.class), q); // [ADDED]

            p = cb.and(p, cb.or(likeName, tokenName, likeOwner, tokenOwner)); // [CHANGED]
            return p;
        };
    }

    private Specification<User> artistSpec(String q) {
        return (root, cq, cb) -> {
            Predicate p = cb.conjunction();
            // ACTIVE + role ARTIST
            p = cb.and(p, cb.equal(root.get("status"), Status.ACTIVE));
            var role = root.join("role", JoinType.LEFT);
            p = cb.and(p, cb.equal(role.get("name"), RoleName.ARTIST));

            // [CHANGED] name LIKE + token-LIKE
            p = cb.and(p, cb.or(
                    likeInsensitive(cb, root.get("name").as(String.class), q),
                    tokensOrLikeInsensitive(cb, root.get("name").as(String.class), q) // [ADDED]
            ));
            return p;
        };
    }

    private Specification<User> profileSpec(String q) {
        return (root, cq, cb) -> {
            Predicate p = cb.conjunction();
            // ACTIVE + role USER
            p = cb.and(p, cb.equal(root.get("status"), Status.ACTIVE));
            var role = root.join("role", JoinType.LEFT);
            p = cb.and(p, cb.equal(role.get("name"), RoleName.USER));

            // [CHANGED] name/email LIKE + token-LIKE
            p = cb.and(p, cb.or(
                    likeInsensitive(cb, root.get("name").as(String.class), q),
                    likeInsensitive(cb, root.get("email").as(String.class), q),
                    tokensOrLikeInsensitive(cb, root.get("name").as(String.class), q),   // [ADDED]
                    tokensOrLikeInsensitive(cb, root.get("email").as(String.class), q)   // [ADDED]
            ));
            return p;
        };
    }

    private Predicate likeInsensitive(CriteriaBuilder cb, Expression<String> expr, String q) {
        if (q == null || q.isBlank()) {
            return cb.conjunction();
        }
        String qLower = "%" + q.toLowerCase() + "%";
        return cb.like(cb.lower(expr), qLower); // MySQL sẽ dùng collation của cột để so sánh
    }

    // [ADDED] LIKE theo token đã normalize/loại stopwords/stemming nhẹ
    private Predicate tokensOrLikeInsensitive(CriteriaBuilder cb, Expression<String> expr, String q) {
        if (q == null || q.isBlank()) return cb.conjunction();

        var toks = new LinkedHashSet<>(NormalizerUtility.tokenize(q));
        if (toks.isEmpty()) {
            String qLower = "%" + NormalizerUtility.normalizeViEn(q) + "%";
            return cb.like(cb.lower(expr), qLower);
        }

        List<Predicate> likes = new ArrayList<>();
        for (String t : toks) {
            if (t.length() < 2) continue; // tránh token quá ngắn
            String pat = "%" + t.toLowerCase() + "%";
            likes.add(cb.like(cb.lower(expr), pat));
        }
        if (likes.isEmpty()) {
            String qLower = "%" + NormalizerUtility.normalizeViEn(q) + "%";
            return cb.like(cb.lower(expr), qLower);
        }
        return cb.or(likes.toArray(new Predicate[0]));
    }

    private String stripAccentsPercent(String s) {
        try {
            String noPct = s.replace("%", "");
            String norm = Normalizer.normalize(noPct, Normalizer.Form.NFD);
            String ascii = norm.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            return "%" + ascii.toLowerCase() + "%";
        } catch (Exception e) {
            return s;
        }
    }

    @Override
    public String findTrackId(String title, String artist) {
        if (title == null || title.isBlank()) return null;

        final var S = Status.PUBLISHED;
        final var T = EntityType.SONGS;

        List<Track> cands = new ArrayList<>();
        // exact/prefix/contains (giữ nguyên)
        cands.addAll(trackRepository.findByStatusAndEntityTypeAndNameIgnoreCase(S, T, title));
        cands.addAll(trackRepository.findByStatusAndEntityTypeAndNameStartingWithIgnoreCaseOrderByPlayCountDesc(
                S, T, title, PageRequest.of(0, 40)));
        cands.addAll(trackRepository.findByStatusAndEntityTypeAndNameContainingIgnoreCaseOrderByPlayCountDesc(
                S, T, title, PageRequest.of(0, 40)));

        // fulltext boolean – kéo ứng viên tốt cho tên ngắn/khác mạo từ
        try {
            String ft = buildFulltextBooleanQuery(title);
            cands.addAll(trackRepository.fulltextBoolean(ft, 60));
        } catch (Exception ignore) {}

        // unique theo id
        Map<String, Track> uniq = new LinkedHashMap<>();
        for (Track t : cands) uniq.putIfAbsent(t.getId(), t);
        cands = new ArrayList<>(uniq.values());
        if (cands.isEmpty()) return null;

        // chuẩn hoá + token hoá
        final String qNorm = NormalizerUtility.normalizeViEn(title);
        final var qTokens = new LinkedHashSet<>(NormalizerUtility.tokenize(title));
        final String qSorted = NormalizerUtility.joinSortedTokens(qTokens);

        final double maxLog = cands.stream()
                .mapToDouble(t -> Math.log1p(Optional.ofNullable(t.getPlayCount()).orElse(0L)))
                .max().orElse(1.0);

        double best = -1.0;
        String bestId = null;

        for (Track t : cands) {
            String nameNorm = NormalizerUtility.normalizeViEn(t.getName());
            var nameTokens = new LinkedHashSet<>(NormalizerUtility.tokenize(t.getName()));
            String nameSorted = NormalizerUtility.joinSortedTokens(nameTokens);

            // 1) JW trên chuỗi chuẩn hoá thô (nhanh, “đi được” nhiều case)
            double jwRaw = SimilarityUtility.jaroWinkler(qNorm, nameNorm);

            // 2) Token-sort JW (đổi thứ tự từ → ổn với case “đảo” từ)
            double jwSorted = SimilarityUtility.jaroWinkler(qSorted, nameSorted);

            // 3) Token-set chồng lấp (Jaccard + Containment)
            double jacc = SimilarityUtility.tokenSetJaccard(qTokens, nameTokens);
            double cont = SimilarityUtility.containment(qTokens, nameTokens);

            // 4) Boost phổ biến & prefix
            double pop = Math.log1p(Optional.ofNullable(t.getPlayCount()).orElse(0L)) / maxLog;
            double prefixBoost = Optional.ofNullable(t.getName())
                    .map(n -> n.toLowerCase().startsWith(title.toLowerCase()) ? 0.05 : 0.0)
                    .orElse(0.0);

            // 5) Hợp nhất điểm – lấy max của hai JW + pha token scores
            double textSim = Math.max(jwRaw, jwSorted);
            double tokenSim = 0.6 * cont + 0.4 * jacc;

            double score = 0.55 * textSim + 0.25 * tokenSim + 0.15 * pop + prefixBoost;

            // Case “bỏ/ thêm mạo từ”: nếu containment >= 0.8 → nới score
            if (cont >= 0.8 && score < 0.72) score = 0.72;

            if (score > best) { best = score; bestId = t.getId(); }
        }

        // Ngưỡng nới nhẹ vì đã có containment check
        return (best >= 0.68) ? bestId : null;
    }

    private String buildFulltextBooleanQuery(String rawTitle) {
        var toks = NormalizerUtility.tokenize(rawTitle);
        if (toks.isEmpty()) return "\"" + NormalizerUtility.normalizeViEn(rawTitle) + "\"";
        return String.join(" ", toks.stream().map(t -> t + "*").toList());
    }

    // -------------------- [ADDED] RERANK HELPERS --------------------

    private record TrackCand(Track t, double score) {}

    private List<Track> rerankTracks(Collection<Track> in, String q, int limit) {
        if (in == null || in.isEmpty()) return List.of();
        final var qTokens = new LinkedHashSet<>(NormalizerUtility.tokenize(q));
        final var qSorted = NormalizerUtility.joinSortedTokens(qTokens);
        final var qNorm   = NormalizerUtility.normalizeViEn(q);

        double maxLog = in.stream()
                .mapToDouble(t -> Math.log1p(Optional.ofNullable(t.getPlayCount()).orElse(0L)))
                .max().orElse(1.0);

        List<TrackCand> scored = new ArrayList<>(in.size());
        for (Track t : in) {
            String nameNorm = NormalizerUtility.normalizeViEn(t.getName());
            var nameTokens = new LinkedHashSet<>(NormalizerUtility.tokenize(t.getName()));
            String nameSorted = NormalizerUtility.joinSortedTokens(nameTokens);

            double jwRaw    = SimilarityUtility.jaroWinkler(qNorm, nameNorm);
            double jwSorted = SimilarityUtility.jaroWinkler(qSorted, nameSorted);
            double jacc     = SimilarityUtility.tokenSetJaccard(qTokens, nameTokens);
            double cont     = SimilarityUtility.containment(qTokens, nameTokens);

            double pop = Math.log1p(Optional.ofNullable(t.getPlayCount()).orElse(0L)) / maxLog;

            double prefixBoost = Optional.ofNullable(t.getName())
                    .map(n -> n.toLowerCase().startsWith(q.toLowerCase()) ? 0.05 : 0.0)
                    .orElse(0.0);

            double textSim  = Math.max(jwRaw, jwSorted);
            double tokenSim = 0.6 * cont + 0.4 * jacc;

            double score = 0.55 * textSim + 0.25 * tokenSim + 0.15 * pop + prefixBoost;
            if (cont >= 0.8 && score < 0.72) score = 0.72;

            scored.add(new TrackCand(t, score));
        }
        scored.sort((a,b)-> Double.compare(b.score, a.score));
        return scored.stream().limit(limit).map(TrackCand::t).toList();
    }

    private <T> List<T> rerankByName(
            Collection<T> in, String q, int limit,
            java.util.function.Function<T,String> nameGetter,
            java.util.function.ToDoubleFunction<T> popularityGetter
    ) {
        if (in == null || in.isEmpty()) return List.of();
        final var qTokens = new LinkedHashSet<>(NormalizerUtility.tokenize(q));
        final var qSorted = NormalizerUtility.joinSortedTokens(qTokens);
        final var qNorm   = NormalizerUtility.normalizeViEn(q);

        double maxPop = in.stream().mapToDouble(popularityGetter).max().orElse(1.0);

        record Cand<T>(T obj, double score) {}
        List<Cand<T>> scored = new ArrayList<>(in.size());
        for (T obj : in) {
            String name = Optional.ofNullable(nameGetter.apply(obj)).orElse("");
            String nameNorm = NormalizerUtility.normalizeViEn(name);
            var nameTokens = new LinkedHashSet<>(NormalizerUtility.tokenize(name));
            String nameSorted = NormalizerUtility.joinSortedTokens(nameTokens);

            double jwRaw    = SimilarityUtility.jaroWinkler(qNorm, nameNorm);
            double jwSorted = SimilarityUtility.jaroWinkler(qSorted, nameSorted);
            double jacc     = SimilarityUtility.tokenSetJaccard(qTokens, nameTokens);
            double cont     = SimilarityUtility.containment(qTokens, nameTokens);

            double textSim  = Math.max(jwRaw, jwSorted);
            double tokenSim = 0.6 * cont + 0.4 * jacc;
            double popNorm  = maxPop <= 0 ? 0 : (popularityGetter.applyAsDouble(obj)/maxPop);

            double score = 0.65 * textSim + 0.25 * tokenSim + 0.10 * popNorm;

            scored.add(new Cand<>(obj, score));
        }
        scored.sort((a,b)-> Double.compare(b.score, a.score));
        return scored.stream().limit(limit).map(c -> c.obj).toList();
    }
}
