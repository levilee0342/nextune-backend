package com.example.nextune_backend.dto.request;

import lombok.Data;

import java.util.List;


@Data
public class TrackIdsRequest {
    private List<String> ids;
}
