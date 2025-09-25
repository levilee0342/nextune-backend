package com.example.nextune_backend.mapper;

import com.example.nextune_backend.dto.request.TrackRequest;
import com.example.nextune_backend.dto.response.TrackResponse;
import com.example.nextune_backend.entity.Album;
import com.example.nextune_backend.entity.Track;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TrackMapper {

    // map từ request -> entity (dùng khi create)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "album", source = "album")
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "playCount", ignore = true)
    @Mapping(target = "explicit", ignore = true)
    @Mapping(target = "lyric", source = "request.lyric")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "imgUrl", source = "request.imgUrl")
    @Mapping(target = "entityType", source = "request.entityType")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "trackOrder", source = "request.trackOrder")
    @Mapping(target = "color", source = "request.color")
    @Mapping(target = "description", source = "request.description")
    Track map(TrackRequest request, Album album);

    // update entity từ request (bỏ qua field null)
    void updateTrackFromRequest(TrackRequest request, @MappingTarget Track track);

    // map entity -> response
    @Mapping(target = "albumId", source = "album.id")
    @Mapping(target = "albumName", source = "album.name")
    @Mapping(target = "artistId",   source = "album.artist.id")
    @Mapping(target = "artistName", source = "album.artist.name")
    TrackResponse map(Track track);

    List<TrackResponse> map(List<Track> tracks);


}
