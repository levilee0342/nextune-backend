package com.example.nextune_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrackQueue {
    private List<String> trackIds;
    private Integer currentIndex;
    private String source;    // ALBUM | PLAYLIST | SIMILAR
    private String sourceId;  // albumId | playlistId | trackId(gốc để gọi similar)
}
