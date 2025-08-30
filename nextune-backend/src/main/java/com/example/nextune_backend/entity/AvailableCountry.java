package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AvailableCountry {
    @EmbeddedId
    private AvailableCountryId id;

    @ManyToOne
    @MapsId("trackId")
    @JoinColumn(name="track_id")
    private Track track;

    @ManyToOne
    @MapsId("countryId")
    @JoinColumn(name="country_id")
    private Country country;
}
