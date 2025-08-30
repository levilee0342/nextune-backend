package com.example.nextune_backend.dto.response;

import lombok.Data;

@Data
public class TrackArtistResponse {
    private String trackId;
    private String artistId;
    private String roleInTrack;
}
