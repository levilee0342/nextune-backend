package com.example.nextune_backend.dto.response;

import com.example.nextune_backend.entity.enums.EntityType;
import com.example.nextune_backend.entity.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackInSavedPlaylistResponse {

    private String id;
    private String name;
    private Integer duration;
    private String imgUrl;
    private String trackUrl;
    private EntityType entityType;
    private Status status;
    private String albumId;
    private String albumName;

}
