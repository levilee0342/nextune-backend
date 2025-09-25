package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.response.SearchResponse;

public interface SearchService {
    SearchResponse search(String q, String types, int limit);
    String findTrackId(String title, String artist);
}
