package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.Comment;
import com.example.nextune_backend.entity.CommentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, CommentId> {
    List<Comment> findByTrack_Id(String trackId);
}

