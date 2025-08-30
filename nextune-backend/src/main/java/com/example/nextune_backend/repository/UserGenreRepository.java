package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.UserGenre;
import com.example.nextune_backend.entity.UserGenreId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGenreRepository extends JpaRepository<UserGenre, UserGenreId> {
    List<UserGenre> findByUser_Id(String userId);
}

