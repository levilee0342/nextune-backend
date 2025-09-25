package com.example.nextune_backend.dto.request;

import com.example.nextune_backend.entity.enums.Status;

import lombok.Data;

@Data
public class PlaylistRequest {

    private String name;
    private String description;
    private String imgUrl;
    private Boolean isPublic;
    private String userId;
    private Status status;
    private String color;
}
