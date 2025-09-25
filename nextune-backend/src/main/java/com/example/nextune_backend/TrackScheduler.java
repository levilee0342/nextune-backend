package com.example.nextune_backend;


import com.example.nextune_backend.service.TrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TrackScheduler {

    private final TrackService trackService;


    public TrackScheduler(TrackService trackService) {
        this.trackService = trackService;
    }

    @Scheduled(cron = "0 * * * * *") // mỗi phút
    public void checkAndPublishTracks() {
        log.info("⏰ Scheduler triggered to check tracks...");
        trackService.publishScheduledTracks();
    }
}