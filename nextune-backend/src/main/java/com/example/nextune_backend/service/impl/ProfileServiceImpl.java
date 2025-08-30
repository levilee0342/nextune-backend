package com.example.nextune_backend.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.nextune_backend.dto.request.ProfileRequest;
import com.example.nextune_backend.dto.response.ProfileResponse;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.mapper.ProfileMapper;
import com.example.nextune_backend.repository.ProfileRepository;

import com.example.nextune_backend.service.ProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Override
    public List<ProfileResponse> getAllProfiles() {

        return profileMapper.map(profileRepository.findAll());
    }

    @Override
    public ProfileResponse getProfileById(String id) {
        return profileMapper.map(profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found")));

    }

    @Override
    public ProfileResponse updateProfileById(String id, ProfileRequest request) {
        User user = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }

        return profileMapper.map(profileRepository.save(user));

    }

}
