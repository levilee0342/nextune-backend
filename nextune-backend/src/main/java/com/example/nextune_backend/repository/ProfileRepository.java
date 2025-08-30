package com.example.nextune_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nextune_backend.entity.User;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<User, String> {
    Optional<User> findById(String id);

    List<User> findAll();

}