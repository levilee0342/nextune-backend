package com.example.nextune_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CloudAsset {
    private String publicId;
    private String url;
    private String format;
    private String resourceType;
    private int width;
    private int height;
    private long bytes;
    private double duration;
}
