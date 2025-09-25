package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.TrackQueue;
import com.example.nextune_backend.service.PlayerQueueService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerQueueServiceImpl implements PlayerQueueService {
    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    private String key(String ownerId) { return "queue:" + ownerId; }

    public void saveQueue(String ownerId, TrackQueue q) {
        try {
            redis.opsForValue().set(key(ownerId), objectMapper.writeValueAsString(q));
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public Optional<TrackQueue> loadQueue(String ownerId) {
        String json = redis.opsForValue().get(key(ownerId));
        if (json == null) return Optional.empty();
        try {
            return Optional.of(objectMapper.readValue(json, TrackQueue.class));
        } catch (Exception e) { return Optional.empty(); }
    }
}
