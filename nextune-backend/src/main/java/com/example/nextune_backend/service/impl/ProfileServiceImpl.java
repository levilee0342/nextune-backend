package com.example.nextune_backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import com.example.nextune_backend.entity.Role;
import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.enums.Status;
import com.example.nextune_backend.repository.RoleRepository;
import org.springframework.context.annotation.Profile;
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
    private final RoleRepository roleRepository;

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
    public String getEmailByUserId(String userId) {
        return profileRepository.findById(userId)
                .map(User::getEmail)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
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
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getDescription() != null) {
            user.setDescription(request.getDescription());
        }
        if (request.getIsPremium() != null) {
            user.setIsPremium(request.getIsPremium());
        }
        if (request.getPremiumDueDate() != null) {
            user.setPremiumDueDate(request.getPremiumDueDate());
        }
        if (request.getViolateCount() != null) {
            user.setViolateCount(request.getViolateCount());
        }


        if (request.getRole() != null && request.getRole().getId() != null) {
            Role role = roleRepository.findById(request.getRole().getId())
                    .orElseThrow(() -> new RuntimeException("Role not found"));
            user.setRole(role);
        }


        if (request.getStatus() != null) {
            try {
                Status newStatus = Status.valueOf(request.getStatus().toString());
                user.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid status value: " + request.getStatus());
            }
        }


        return profileMapper.map(profileRepository.save(user));

    }

    @Override
    public void revokeScheduledPremium() {
        List<User> premiumUser = profileRepository.findByIsPremium(true);
        LocalDateTime now = LocalDateTime.now();

        System.out.println("🔍 Found {} premium users at {}"+ premiumUser.size() + now);

        for (User user : premiumUser) {
            if (user.getPremiumDueDate() != null && !user.getPremiumDueDate().isAfter(now)) {
                System.out.println("✅ Track [{} - {}] is ready. Changing status to PUBLISHED"+ user.getEmail() +  user.getIsPremium());
                user.setIsPremium(false);
                user.setPremiumDueDate(null);

                profileRepository.save(user);
            } else {
                System.out.println("⏳ Track [{} - {}] is not yet ready (publishedAt = {})"+
                        user.getId()+ user.getName()+ user.getPremiumDueDate());
            }
        }
    }

}
