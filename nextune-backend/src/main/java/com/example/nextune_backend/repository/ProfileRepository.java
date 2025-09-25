package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nextune_backend.entity.User;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<User, String> {
    Optional<User> findById(String id);

    List<User> findAll();

    List<User> findByIsPremium(Boolean isPremium);
}