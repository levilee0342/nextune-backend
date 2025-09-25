package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.entity.enums.EntityType;
import com.example.nextune_backend.repository.TrackRepository;
import com.example.nextune_backend.service.SimilarService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimilarServiceImpl implements SimilarService {
    private final WebClient webClient;
    private final TrackRepository trackRepository;

    public List<String> getSimilarTrackIds(EntityType entityType, String trackId, int limit) {
        String et = entityType.name(); // e.g. SONGS | PODCASTS
        return webClient.get()
                .uri("/similar-tracks/{entity_type}/{track_id}", et, trackId)
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .map(node -> node.get("track_id").asText())
                .take(limit) // nếu muốn giới hạn
                .collectList()
                .blockOptional()
                .orElseGet(List::of);
    }
}
