package com.example.nextune_backend.dto.request;

import lombok.Data;

@Data
public class CommentRequest {
    private String trackId;
    private String content;
}
