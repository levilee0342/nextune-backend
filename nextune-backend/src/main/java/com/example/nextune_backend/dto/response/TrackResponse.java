package com.example.nextune_backend.dto.response;

import com.example.nextune_backend.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackResponse {
    private String id;
    private String name;
    private Integer duration;
    private String imgUrl;
    private String trackUrl;
    private String entityType;
    private Status status;
    private String albumId;
    private String albumName;
}
