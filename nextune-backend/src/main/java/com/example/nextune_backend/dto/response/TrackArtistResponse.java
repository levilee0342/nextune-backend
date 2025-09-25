package com.example.nextune_backend.dto.response;

import lombok.Data;

@Data
public class TrackArtistResponse {
    private String trackId;
    private String artistId;
    private String roleInTrack;
    private String trackName;
    private String imgUrl;
    private Long playCount;
    private Integer duration;
}
