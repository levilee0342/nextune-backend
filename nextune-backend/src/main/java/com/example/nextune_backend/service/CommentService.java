package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.CommentRequest;
import com.example.nextune_backend.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse createComment(CommentRequest request);
    CommentResponse updateComment(CommentRequest request);
    void deleteComment(String trackId, String userId);
    List<CommentResponse> getCommentsByTrack(String trackId);
    CommentResponse getCommentById(String trackId, String userId);
    List<CommentResponse> getAllComments();
}

