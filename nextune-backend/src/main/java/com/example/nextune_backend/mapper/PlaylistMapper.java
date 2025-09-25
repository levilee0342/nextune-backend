package com.example.nextune_backend.mapper;


import com.example.nextune_backend.dto.request.PlaylistRequest;
import com.example.nextune_backend.dto.response.PlaylistResponse;
import com.example.nextune_backend.entity.Playlist;
import com.example.nextune_backend.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PlaylistMapper {

    // create
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "status", source = "request.status")
    Playlist toEntity(PlaylistRequest request, User user);

    // update
    void updateEntity(PlaylistRequest request, @MappingTarget Playlist playlist);

    // entity -> response
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "ownerName", source = "user.name")
    PlaylistResponse toResponse(Playlist playlist);

    List<PlaylistResponse> toResponseList(List<Playlist> playlists);


}
