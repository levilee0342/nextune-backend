package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.UserGenreRequest;
import com.example.nextune_backend.dto.response.UserGenreResponse;

import java.util.List;

public interface UserGenreService {
    UserGenreResponse addGenreToUser(UserGenreRequest request);
    void removeGenreFromUser(String userId, String genreId);
    List<UserGenreResponse> getGenresByUser(String userId);
}
