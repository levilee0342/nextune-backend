package com.example.nextune_backend.dto.response;

import lombok.Data;

@Data
public class UserGenreResponse {
    private String userId;
    private String userName;
    private String genreId;
    private String genreName;
}