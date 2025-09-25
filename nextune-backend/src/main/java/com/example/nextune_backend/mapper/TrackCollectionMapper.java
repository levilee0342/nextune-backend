package com.example.nextune_backend.mapper;

import com.example.nextune_backend.dto.request.TrackCollectionRequest;
import com.example.nextune_backend.dto.response.TrackCollectionResponse;
import com.example.nextune_backend.entity.Playlist;
import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.TrackCollection;
import com.example.nextune_backend.entity.TrackCollectionId;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TrackCollectionMapper {

    @Mapping(target = "id", expression = "java(new com.example.nextune_backend.entity.TrackCollectionId(playlist.getId(), track.getId()))")
    @Mapping(target = "playlist", source = "playlist")
    @Mapping(target = "track", source = "track")
    @Mapping(target = "addedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "trackOrder", source = "request.trackOrder")
    TrackCollection toEntity(TrackCollectionRequest request, Playlist playlist, Track track);

    @Mapping(target = "trackId", source = "track.id")
    @Mapping(target = "playlistId", source = "playlist.id")
    @Mapping(target = "trackName", source = "track.name")
    @Mapping(target = "trackImgUrl", source = "track.imgUrl")
    @Mapping(target = "trackDuration", source = "track.duration")
    @Mapping(target = "publishedAt", source = "track.publishedAt")
    @Mapping(target = "artistId",   source = "track.album.artist.id")
    @Mapping(target = "artistName", source = "track.album.artist.name")
    TrackCollectionResponse toResponse(TrackCollection entity);

    List<TrackCollectionResponse> toResponseList(List<TrackCollection> entities);
}
