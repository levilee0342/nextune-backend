package com.example.nextune_backend.mapper;

import com.example.nextune_backend.dto.request.UserGenreRequest;
import com.example.nextune_backend.dto.response.UserGenreResponse;
import com.example.nextune_backend.entity.Genre;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.entity.UserGenre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserGenreMapper {

    @Mapping(target = "id", expression = "java(new com.example.nextune_backend.entity.UserGenreId(request.getUserId(), request.getGenreId()))")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "genre", source = "genre")
    UserGenre toEntity(UserGenreRequest request, User user, Genre genre);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.name")
    @Mapping(target = "genreId", source = "genre.id")
    @Mapping(target = "genreName", source = "genre.name")
    UserGenreResponse toResponse(UserGenre entity);

    List<UserGenreResponse> toResponseList(List<UserGenre> entities);
}
