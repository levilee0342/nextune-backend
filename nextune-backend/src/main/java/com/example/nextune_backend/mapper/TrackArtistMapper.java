package com.example.nextune_backend.mapper;

import com.example.nextune_backend.dto.request.TrackArtistRequest;
import com.example.nextune_backend.dto.response.ProfileResponse;
import com.example.nextune_backend.dto.response.TrackArtistResponse;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.TrackArtist;
import com.example.nextune_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrackArtistMapper {

    @Mapping(target = "id", expression = "java(new com.example.nextune_backend.entity.TrackArtistId(request.getTrackId(), request.getArtistId()))")
    @Mapping(target = "track", source = "track")
    @Mapping(target = "artist", source = "artist")
    TrackArtist toEntity(TrackArtistRequest request, Track track, User artist);


    @Mapping(target = "trackId", source = "track.id")
    @Mapping(target = "artistId", source = "artist.id")
    @Mapping(target = "roleInTrack", source = "roleInTrack")
    @Mapping(target = "trackName", source = "track.name")
    @Mapping(target = "imgUrl", source = "track.imgUrl")
    @Mapping(target = "playCount", source = "track.playCount")
    @Mapping(target = "duration", source = "track.duration")
    TrackArtistResponse toResponse(TrackArtist entity);

    List<TrackArtistResponse> toResponseList(List<TrackArtist> entities);

    // Cập nhật roleInTrack
    @Mapping(target = "track", ignore = true)
    @Mapping(target = "artist", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateFromRequest(TrackArtistRequest request, @MappingTarget TrackArtist entity);
}


