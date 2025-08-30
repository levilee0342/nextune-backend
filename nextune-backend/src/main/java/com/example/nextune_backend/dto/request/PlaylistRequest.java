package com.example.nextune_backend.dto.request;

import lombok.Data;

@Data
public class PlaylistRequest {
    private String name;
    private String description;
    private String imgUrl;
    private String status;
}
