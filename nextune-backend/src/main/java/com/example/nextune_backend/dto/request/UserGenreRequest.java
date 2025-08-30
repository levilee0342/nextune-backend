package com.example.nextune_backend.dto.request;

import lombok.Data;

@Data
public class UserGenreRequest {
    private String userId;
    private String genreId;
}
