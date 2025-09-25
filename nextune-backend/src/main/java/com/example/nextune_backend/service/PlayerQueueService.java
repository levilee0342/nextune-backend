package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.TrackQueue;

import java.util.Optional;

public interface PlayerQueueService {
    void saveQueue(String ownerId, TrackQueue q);
    Optional<TrackQueue> loadQueue(String ownerId);
}
