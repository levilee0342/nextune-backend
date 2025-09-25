package com.example.nextune_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackPlayResponse {
    private String trackUrl;
    private AlbumResponse album;        // nullable
    private PlaylistResponse playlist;  // nullable
    private TrackResponse track;
    private List<String> queueIds;
    private Integer currentIndex;
    private String source;
    private String sourceId;
}
