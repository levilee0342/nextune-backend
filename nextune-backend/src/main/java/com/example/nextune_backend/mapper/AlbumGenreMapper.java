package com.example.nextune_backend.mapper;

import com.example.nextune_backend.dto.request.AlbumGenreRequest;
import com.example.nextune_backend.dto.response.AlbumGenreResponse;
import com.example.nextune_backend.entity.Album;
import com.example.nextune_backend.entity.AlbumGenre;
import com.example.nextune_backend.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlbumGenreMapper {

    @Mapping(target = "id", expression = "java(new com.example.nextune_backend.entity.AlbumGenreId(request.getAlbumId(), request.getGenreId()))")
    @Mapping(target = "album", source = "album")
    @Mapping(target = "genre", source = "genre")
    AlbumGenre toEntity(AlbumGenreRequest request, Album album, Genre genre);

    @Mapping(target = "albumId", source = "album.id")
    @Mapping(target = "albumName", source = "album.name")
    @Mapping(target = "genreId", source = "genre.id")
    @Mapping(target = "genreName", source = "genre.name")
    AlbumGenreResponse toResponse(AlbumGenre entity);

    List<AlbumGenreResponse> toResponseList(List<AlbumGenre> entities);
}

