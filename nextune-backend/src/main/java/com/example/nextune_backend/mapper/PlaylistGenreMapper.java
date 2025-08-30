package com.example.nextune_backend.mapper;

import com.example.nextune_backend.dto.request.PlaylistGenreRequest;
import com.example.nextune_backend.dto.response.PlaylistGenreResponse;
import com.example.nextune_backend.entity.Genre;
import com.example.nextune_backend.entity.Playlist;
import com.example.nextune_backend.entity.PlaylistGenre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlaylistGenreMapper {

    @Mapping(target = "id", expression = "java(new com.example.nextune_backend.entity.PlaylistGenreId(request.getPlaylistId(), request.getGenreId()))")
    @Mapping(target = "playlist", source = "playlist")
    @Mapping(target = "genre", source = "genre")
    PlaylistGenre toEntity(PlaylistGenreRequest request, Playlist playlist, Genre genre);

    @Mapping(target = "playlistId", source = "playlist.id")
    @Mapping(target = "playlistName", source = "playlist.name")
    @Mapping(target = "genreId", source = "genre.id")
    @Mapping(target = "genreName", source = "genre.name")
    PlaylistGenreResponse toResponse(PlaylistGenre entity);

    List<PlaylistGenreResponse> toResponseList(List<PlaylistGenre> entities);
}
