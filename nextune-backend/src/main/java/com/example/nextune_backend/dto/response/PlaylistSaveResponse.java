package com.example.nextune_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistSaveResponse {
    private String userId;
    private String playlistId;
    private LocalDateTime createdAt;
    private PlaylistSummary playlist;

    @Data
    @Builder
    @AllArgsConstructor @NoArgsConstructor
    public static class PlaylistSummary {
        private String id;
        private String name;
        private String imgUrl;
        private Integer totalTracks;
        private Integer totalDuration;
        private Integer totalFollowers;
        private Boolean isPublic;
    }
}