package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.Subscription;
import com.example.nextune_backend.entity.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    List<Subscription> findByStatus(SubscriptionStatus status);
    Optional<Subscription> findByNameAndStatus(String name, SubscriptionStatus status);
}

