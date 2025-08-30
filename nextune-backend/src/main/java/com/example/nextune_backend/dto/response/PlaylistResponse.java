package com.example.nextune_backend.dto.response;

import lombok.Data;

@Data
public class PlaylistResponse {
    private String id;
    private String name;
    private String description;
    private String imgUrl;
    private String userId;
}