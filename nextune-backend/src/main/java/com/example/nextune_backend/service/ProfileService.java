package com.example.nextune_backend.service;

import java.util.List;

import com.example.nextune_backend.dto.request.ProfileRequest;
import com.example.nextune_backend.dto.response.ProfileResponse;

public interface ProfileService {
    ProfileResponse getProfileById(String id);

    List<ProfileResponse> getAllProfiles();

    ProfileResponse updateProfileById(String id, ProfileRequest request);

    String getEmailByUserId(String userId);
    void revokeScheduledPremium();
}
