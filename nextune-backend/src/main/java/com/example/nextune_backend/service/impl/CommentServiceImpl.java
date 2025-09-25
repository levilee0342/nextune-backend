package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.CommentRequest;
import com.example.nextune_backend.dto.response.CommentResponse;
import com.example.nextune_backend.entity.Comment;
import com.example.nextune_backend.entity.CommentId;
import com.example.nextune_backend.entity.Track;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.mapper.CommentMapper;
import com.example.nextune_backend.repository.CommentRepository;
import com.example.nextune_backend.repository.TrackRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.CommentService;
import com.example.nextune_backend.utility.UserUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final UserUtility userUtility;

    @Override
    public CommentResponse createComment(CommentRequest request) {
        Track track = trackRepository.findById(request.getTrackId())
                .orElseThrow(() -> new RuntimeException("Track not found"));
        User user = userRepository.findById(userUtility.getCurrentUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = commentMapper.toEntity(request, track, user);
        commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }

    @Override
    public CommentResponse updateComment(CommentRequest request) {
        CommentId id = new CommentId(request.getTrackId(), userUtility.getCurrentUserId());
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        commentMapper.updateFromRequest(request, comment);
        commentRepository.save(comment);

        return commentMapper.toResponse(comment);
    }

    @Override
    public void deleteComment(String trackId, String userId) {
        CommentId id = new CommentId(trackId, userId);
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepository.deleteById(id);
    }

    @Override
    public List<CommentResponse> getCommentsByTrack(String trackId) {
        return commentMapper.toResponseList(commentRepository.findByTrack_Id(trackId));
    }

    @Override
    public CommentResponse getCommentById(String trackId, String userId) {
        CommentId id = new CommentId(trackId, userId);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return commentMapper.toResponse(comment);
    }

    @Override
    public List<CommentResponse> getAllComments() {
        return commentMapper.toResponseList(commentRepository.findAll());
    }
}
