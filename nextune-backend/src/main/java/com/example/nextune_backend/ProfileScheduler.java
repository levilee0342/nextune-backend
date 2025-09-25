package com.example.nextune_backend;

import com.example.nextune_backend.service.ProfileService;
import com.example.nextune_backend.service.TrackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ProfileScheduler {

    private final ProfileService profileService;


    public ProfileScheduler(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Scheduled(cron = "0 * * * * *") // mỗi phút
    public void checkAndRevokePremium() {
        log.info("⏰ Scheduler triggered to check users...");
        profileService.revokeScheduledPremium();
    }
}
