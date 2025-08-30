package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.UserGenreRequest;
import com.example.nextune_backend.dto.response.UserGenreResponse;
import com.example.nextune_backend.service.UserGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-genre")
@RequiredArgsConstructor
public class UserGenreController {

    private final UserGenreService userGenreService;

    @PostMapping
    public ResponseEntity<UserGenreResponse> addGenreToUser(@RequestBody UserGenreRequest request) {
        return ResponseEntity.ok(userGenreService.addGenreToUser(request));
    }

    @DeleteMapping("/{userId}/{genreId}")
    public ResponseEntity<String> removeGenreFromUser(@PathVariable String userId,
                                                      @PathVariable String genreId) {
        userGenreService.removeGenreFromUser(userId, genreId);
        return ResponseEntity.ok("Removed genre " + genreId + " from user " + userId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<UserGenreResponse>> getGenresByUser(@PathVariable String userId) {
        return ResponseEntity.ok(userGenreService.getGenresByUser(userId));
    }
}

