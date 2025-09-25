package com.example.nextune_backend.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private String trackId;
    private String userId;
    private String content;
    private LocalDateTime commentDate;
}
