package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.TrackArtistRequest;
import com.example.nextune_backend.dto.response.ProfileResponse;
import com.example.nextune_backend.dto.response.TrackArtistResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.TrackArtist;
import com.example.nextune_backend.entity.User;

import java.util.List;

public interface TrackArtistService {
    TrackArtistResponse assignArtistToTrack(TrackArtistRequest trackArtistRequest);

    TrackArtistResponse updateArtistRole(TrackArtistRequest request);

    void removeArtistFromTrack(String trackId, String artistId);

    List<TrackArtistResponse> getArtistsByTrack(String trackId);

    //List<TrackArtistResponse> getTracksByArtist(String artistId);

     List<TrackResponse> getTracksByArtist(String artistId);
}
