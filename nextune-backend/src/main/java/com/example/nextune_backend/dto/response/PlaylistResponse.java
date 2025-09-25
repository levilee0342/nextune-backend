package com.example.nextune_backend.dto.response;

import java.time.LocalDateTime;

import com.example.nextune_backend.entity.enums.Status;

import lombok.Data;

@Data
public class PlaylistResponse {
    private String id;
    private String name;
    private String description;
    private String imgUrl;
    private Boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer totalDuration;
    private Integer totalTracks;
    private Integer totalFollowers;
    private Status status;
    private LocalDateTime createAt;
    private String color;
    private Boolean isProfile;
    private String userId;
    private String ownerName;
}