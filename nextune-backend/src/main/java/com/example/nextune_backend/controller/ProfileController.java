package com.example.nextune_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.nextune_backend.dto.request.ProfileRequest;
import com.example.nextune_backend.dto.response.ApiResponse;
import com.example.nextune_backend.dto.response.ProfileResponse;

import com.example.nextune_backend.service.ProfileService;

import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public ApiResponse<List<ProfileResponse>> getAllProfiles() {
        List<ProfileResponse> users = profileService.getAllProfiles();
        return ApiResponse.<List<ProfileResponse>>builder()
                .result(users)
                .message("All profile fetched successfully")
                .status(200)
                .success(true)
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProfileResponse> getProfileById(@PathVariable String id) {
        ProfileResponse user = profileService.getProfileById(id);
        return ApiResponse.<ProfileResponse>builder()
                .result(user)
                .message("Profile fetched successfully")
                .status(200)
                .success(true)
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ProfileResponse> updateProfile(@PathVariable String id, @RequestBody ProfileRequest request) {
       

        ProfileResponse user = profileService.updateProfileById(id, request);

        return ApiResponse.<ProfileResponse>builder()
                .result(user)
                .message("Profile updated successfully")
                .status(200)
                .success(true)
                .build();

    }

}
