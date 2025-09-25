package com.example.nextune_backend.dto.request;

import lombok.Data;

@Data
public class PlaylistSaveRequest {
    private String playlistId; // userId lấy từ context
}