package com.example.nextune_backend.dto.request;

import lombok.Data;
import lombok.Getter;

@Data
public class TrackCollectionRequest {
  private String trackId;
  private String playlistId;
  private Integer trackOrder;

}
