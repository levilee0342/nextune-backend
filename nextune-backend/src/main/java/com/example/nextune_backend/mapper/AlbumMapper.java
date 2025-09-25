package com.example.nextune_backend.mapper;

import com.example.nextune_backend.dto.request.AlbumRequest;
import com.example.nextune_backend.dto.request.ProfileRequest;
import com.example.nextune_backend.dto.response.AlbumResponse;
import com.example.nextune_backend.dto.response.ProfileResponse;
import com.example.nextune_backend.entity.Album;
import com.example.nextune_backend.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AlbumMapper {
    @Mapping(target = "artistId",   source = "artist.id")
    @Mapping(target = "artistName", source = "artist.name")
    AlbumResponse map(Album album);

    List<AlbumResponse> map(List<Album> albums);

    Album map(AlbumRequest request);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAlbumFromRequest(AlbumRequest request, @MappingTarget Album album);


}