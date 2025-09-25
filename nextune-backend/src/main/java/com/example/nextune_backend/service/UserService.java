package com.example.nextune_backend.service;


import com.example.nextune_backend.dto.GoogleUser;
import com.example.nextune_backend.dto.GoogleUserProfile;
import com.example.nextune_backend.entity.Role;
import com.example.nextune_backend.entity.User;

public interface UserService {
    User upsertGoogleUser(GoogleUserProfile g); // find-or-create + link googleSub
    User getById(String id);
}
