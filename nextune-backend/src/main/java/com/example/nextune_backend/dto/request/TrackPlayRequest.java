package com.example.nextune_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackPlayRequest {
    private String albumId;    // optional
    private String playlistId; // optional
}