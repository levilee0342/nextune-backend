package com.example.nextune_backend.mapper;

import com.example.nextune_backend.dto.request.CommentRequest;
import com.example.nextune_backend.dto.response.CommentResponse;
import com.example.nextune_backend.entity.Comment;
import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", expression = "java(new com.example.nextune_backend.entity.CommentId(request.getTrackId(), user.getId()))")
    @Mapping(target = "track", source = "track")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "commentDate", expression = "java(java.time.LocalDateTime.now())")
    Comment toEntity(CommentRequest request, Track track, User user);

    @Mapping(target = "trackId", source = "track.id")
    @Mapping(target = "userId", source = "user.id")
    CommentResponse toResponse(Comment entity);

    List<CommentResponse> toResponseList(List<Comment> entities);

    @Mapping(target = "track", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "commentDate", expression = "java(java.time.LocalDateTime.now())")
    void updateFromRequest(CommentRequest request, @MappingTarget Comment entity);
}
