package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.response.SearchResponse;
import com.example.nextune_backend.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    // /search?q=...&types=all|song,album,artist,playlist,podcast,profile&limit=5
    @GetMapping
    public ResponseEntity<SearchResponse> search(
            @RequestParam("q") String q,
            @RequestParam(defaultValue = "all") String types,  // comma separated hoặc "all"
            @RequestParam(defaultValue = "5") int limit
    ) {
        SearchResponse res = searchService.search(q, types, limit);
        return ResponseEntity.ok(res);
    }
}
