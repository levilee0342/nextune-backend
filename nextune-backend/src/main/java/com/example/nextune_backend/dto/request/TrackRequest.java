package com.example.nextune_backend.dto.request;


import com.example.nextune_backend.entity.Album;
import com.example.nextune_backend.entity.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class TrackRequest {
    private String name;
    private Integer duration;
    private String imgUrl;
    private String trackUrl;
    private String entityType;
    private String albumId;
    private Status status = Status.PUBLISHED;
}

