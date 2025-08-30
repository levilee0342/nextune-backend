package com.example.nextune_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy=GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name="user_id")
    User user;

    String content;

    @Column(name = "created_date")
    Date createdDate;

    @Enumerated(EnumType.STRING)
    Status status;

    @Column(name="entity_id")
    String entityId;

    @Column(name="report_type")
    String reportType;
}
