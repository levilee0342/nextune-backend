package com.example.nextune_backend.dto.request;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class TrackCollectionRequest {
  private String trackId;
  private List<String> playlistIds;

  private Integer trackOrder;

}
