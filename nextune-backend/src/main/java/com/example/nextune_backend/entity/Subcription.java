package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name ="subcriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subcription {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    String price;
    int duration;

    @Enumerated
    Status status;

    @Column(name ="released_date")
    Date releasedDate;
}
