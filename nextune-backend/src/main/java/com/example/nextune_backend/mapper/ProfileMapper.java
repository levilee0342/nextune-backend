package com.example.nextune_backend.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.nextune_backend.dto.request.ProfileRequest;

import com.example.nextune_backend.dto.response.ProfileResponse;
import com.example.nextune_backend.entity.User;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileResponse map(User user);

    List<ProfileResponse> map(List<User> users);

    User map(ProfileRequest request);

}
