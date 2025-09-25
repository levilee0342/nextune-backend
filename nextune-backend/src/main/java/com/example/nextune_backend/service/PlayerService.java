package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.response.TrackPlayResponse;

public interface PlayerService {
    void play();
    void pause();
    TrackPlayResponse next();
    TrackPlayResponse prev();
    void seek(int offsetMs, boolean relative);

    void setVolume(int level);
    void playTrackId(String trackId);
}