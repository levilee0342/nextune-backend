package com.example.nextune_backend.dto.request;

import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.TrackArtistId;
import com.example.nextune_backend.entity.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class TrackArtistRequest {
    private String trackId;
    private String artistId;
    private String roleInTrack;
}
