package com.example.nextune_backend.service;

import com.example.nextune_backend.entity.enums.EntityType;

import java.util.List;

public interface SimilarService {
    List<String> getSimilarTrackIds(EntityType entityType, String trackId, int limit);
}
