package com.example.nextune_backend.mapper;
import com.example.nextune_backend.entity.Playlist;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.entity.PlaylistSave;
import com.example.nextune_backend.dto.response.PlaylistSaveResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlaylistSaveMapper {

    @Mapping(target = "id", expression = "java(new com.example.nextune_backend.entity.PlaylistSaveId(user.getId(), playlist.getId()))")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "playlist", source = "playlist")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    PlaylistSave toEntity(User user, Playlist playlist);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "playlistId", source = "playlist.id")
    @Mapping(target = "playlist", source = "playlist")
    PlaylistSaveResponse toResponse(PlaylistSave entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "imgUrl", source = "imgUrl")
    @Mapping(target = "totalTracks", source = "totalTracks")
    @Mapping(target = "totalDuration", source = "totalDuration")
    @Mapping(target = "isPublic", source = "isPublic")
    PlaylistSaveResponse.PlaylistSummary toSummary(Playlist playlist);

    java.util.List<PlaylistSaveResponse> toResponseList(java.util.List<PlaylistSave> entities);
}
