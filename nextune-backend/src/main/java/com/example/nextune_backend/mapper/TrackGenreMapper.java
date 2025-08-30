package com.example.nextune_backend.mapper;

import com.example.nextune_backend.dto.request.TrackGenreRequest;
import com.example.nextune_backend.dto.response.TrackGenreResponse;
import com.example.nextune_backend.entity.Genre;
import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.TrackGenre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrackGenreMapper {

    @Mapping(target = "id", expression = "java(new com.example.nextune_backend.entity.TrackGenreId(request.getTrackId(), request.getGenreId()))")
    @Mapping(target = "track", source = "track")
    @Mapping(target = "genre", source = "genre")
    TrackGenre toEntity(TrackGenreRequest request, Track track, Genre genre);

    @Mapping(target = "trackId", source = "track.id")
    @Mapping(target = "trackName", source = "track.name")
    @Mapping(target = "genreId", source = "genre.id")
    @Mapping(target = "genreName", source = "genre.name")
    TrackGenreResponse toResponse(TrackGenre entity);

    List<TrackGenreResponse> toResponseList(List<TrackGenre> entities);
}

