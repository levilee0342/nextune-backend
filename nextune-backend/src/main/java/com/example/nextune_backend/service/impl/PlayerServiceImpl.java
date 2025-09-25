package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.TrackQueue;
import com.example.nextune_backend.dto.response.AlbumResponse;
import com.example.nextune_backend.dto.response.PlaylistResponse;
import com.example.nextune_backend.dto.response.TrackPlayResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.enums.EntityType;
import com.example.nextune_backend.repository.TrackRepository;
import com.example.nextune_backend.service.*;
import com.example.nextune_backend.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerQueueService queueService;
    private final TrackRepository trackRepository;
    private final SimilarService similarService;
    private final AlbumService albumService;
    private final PlaylistService playlistService;
    private final TrackService trackService;
    private final UserUtility userUtility;

    @Override
    public void play() { System.out.println("[Player] play/resume"); }

    @Override
    public void pause() { System.out.println("[Player] pause"); }

    @Transactional
    @Override
    public TrackPlayResponse next() {
        String ownerId = userUtility.getCurrentUserId();
        TrackQueue q = queueService.loadQueue(ownerId)
                .orElseThrow(() -> new IllegalStateException("Queue not found"));

        System.out.println(q);
        System.out.println("Before next play: " +q.getCurrentIndex());

        // nếu còn bài phía sau → tăng index
        if (q.getCurrentIndex() < q.getTrackIds().size() - 1) {
            q.setCurrentIndex(q.getCurrentIndex() + 1);
        } else {
            // Hết queue → mở rộng tùy source
            if ("ALBUM".equals(q.getSource())) {
                // có thể loop về đầu hoặc không làm gì.
                // Ở đây chọn: không đổi (hoặc tự append phần đầu nếu muốn loop).
            } else if ("PLAYLIST".equals(q.getSource())) {
                // tương tự ALBUM
            } else {
                // SIMILAR → gọi thêm similar từ bài cuối để nối thêm
                String lastId = q.getTrackIds().get(q.getTrackIds().size() - 1);
                EntityType et = trackRepository.findById(lastId)
                        .map(Track::getEntityType)
                        .orElse(EntityType.SONGS);
                List<String> more = similarService.getSimilarTrackIds(et, lastId, 20);
                if (!more.isEmpty()) {
                    q.getTrackIds().addAll(more);
                    q.setCurrentIndex(q.getCurrentIndex() + 1);
                }
            }
        }
        System.out.println("Index: "+q.getCurrentIndex());
        queueService.saveQueue(ownerId, q);
        String currentId = q.getTrackIds().get(q.getCurrentIndex());
        return buildResponseForCurrent(currentId, q);
    }

    @Transactional
    @Override
    public TrackPlayResponse prev() {
        String ownerId = userUtility.getCurrentUserId();
        TrackQueue q = queueService.loadQueue(ownerId)
                .orElseThrow(() -> new IllegalStateException("Queue not found"));
        if (q.getCurrentIndex() > 0) q.setCurrentIndex(q.getCurrentIndex() - 1);
        queueService.saveQueue(ownerId, q);
        String currentId = q.getTrackIds().get(q.getCurrentIndex());
        return buildResponseForCurrent(currentId, q);
    }

    private TrackPlayResponse buildResponseForCurrent(String trackId, TrackQueue q) {
        Track track = trackRepository.getById(trackId);
        track.setPlayCount(track.getPlayCount() + 1);
        TrackResponse tr = trackService.getTrackByIdResponse(trackId);
        Object album = null, playlist = null;
        if ("ALBUM".equals(q.getSource())) {
            try { album = /* albumService.getAlbumById(q.getSourceId()) */ null; } catch (Exception ignored) {}
        } else if ("PLAYLIST".equals(q.getSource())) {
            try { playlist = /* playlistService.getPlaylistById(q.getSourceId()) */ null; } catch (Exception ignored) {}
        }
        return TrackPlayResponse.builder()
                .trackUrl(tr.getTrackUrl())
                .album((AlbumResponse) album)
                .playlist((PlaylistResponse) playlist)
                .track(tr)
                .queueIds(q.getTrackIds())
                .currentIndex(q.getCurrentIndex())
                .source(q.getSource())
                .sourceId(q.getSourceId())
                .build();
    }

    @Override
    public void seek(int offsetMs, boolean relative) {
        System.out.println("[Player] seek offsetMs=" + offsetMs + " relative=" + relative);
    }

    @Override
    public void setVolume(int level) { System.out.println("[Player] volume=" + level); }

    @Override
    public void playTrackId(String trackId) { System.out.println("[Player] play trackId=" + trackId); }
}
