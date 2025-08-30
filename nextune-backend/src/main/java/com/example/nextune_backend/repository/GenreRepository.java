package com.example.nextune_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nextune_backend.entity.Genre;

public interface GenreRepository extends JpaRepository<Genre, String> {
    List<Genre> findAll();

    Optional<Genre> findById(String id);
}
