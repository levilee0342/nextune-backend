package com.example.nextune_backend.mapper;

import com.example.nextune_backend.dto.request.GenreRequest;
import com.example.nextune_backend.dto.response.GenreResponse;
import com.example.nextune_backend.entity.Genre;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    Genre toEntity(GenreRequest request);

    GenreResponse toResponse(Genre genre);

    List<GenreResponse> toResponseList(List<Genre> genres);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateGenreFromRequest(GenreRequest request, @MappingTarget Genre genre);
}
