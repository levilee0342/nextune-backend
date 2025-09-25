package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.CommentRequest;
import com.example.nextune_backend.dto.request.TrackGenreRequest;
import com.example.nextune_backend.dto.response.CommentResponse;
import com.example.nextune_backend.dto.response.TrackGenreResponse;
import com.example.nextune_backend.service.CommentService;
import com.example.nextune_backend.service.TrackGenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // Lấy tất cả comments
    @GetMapping
    public ResponseEntity<List<CommentResponse>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    // Lấy comments theo track
    @GetMapping("/track/{trackId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByTrack(@PathVariable String trackId) {
        return ResponseEntity.ok(commentService.getCommentsByTrack(trackId));
    }

    // Lấy comment theo ID (trackId + userId)
    @GetMapping("/{trackId}/{userId}")
    public ResponseEntity<CommentResponse> getCommentById(@PathVariable String trackId,
                                                          @PathVariable String userId) {
        return ResponseEntity.ok(commentService.getCommentById(trackId, userId));
    }

    // Tạo mới comment
    @PostMapping
    public ResponseEntity<CommentResponse> createComment(@RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.createComment(request));
    }

    // Update comment
    @PutMapping
    public ResponseEntity<CommentResponse> updateComment(@RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.updateComment(request));
    }

    // Delete comment
    @DeleteMapping("/{trackId}/{userId}")
    public ResponseEntity<Void> deleteComment(@PathVariable String trackId,
                                              @PathVariable String userId) {
        commentService.deleteComment(trackId, userId);
        return ResponseEntity.noContent().build();
    }
}
