package com.example.nextune_backend.mapper;

import com.example.nextune_backend.dto.request.RegisterRequest;
import com.example.nextune_backend.dto.response.RegisterResponse;
import com.example.nextune_backend.entity.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RegisterUserMapper {
    RegisterResponse map(User user);

    List<RegisterResponse> map(List<User> users);

    User map(RegisterRequest request);
}
