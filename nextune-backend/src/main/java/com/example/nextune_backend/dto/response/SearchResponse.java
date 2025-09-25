package com.example.nextune_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResponse {
    private String query;
    private List<TrackResponse> songs;
    private List<AlbumResponse> albums;
    private List<UserCard> artists;
    private List<PlaylistResponse> playlists;
    private List<AlbumResponse> podcastShows;
    private List<TrackResponse> podcastEpisodes;
    private List<UserCard> profiles;     
}