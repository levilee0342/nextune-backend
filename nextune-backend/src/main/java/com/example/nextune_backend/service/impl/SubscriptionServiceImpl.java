package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.SubscriptionRequest;
import com.example.nextune_backend.dto.response.SubscriptionResponse;
import com.example.nextune_backend.entity.Subscription;
import com.example.nextune_backend.entity.enums.SubscriptionStatus;
import com.example.nextune_backend.repository.SubscriptionRepository;
import com.example.nextune_backend.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepo;


    @Override
    public List<SubscriptionResponse> listActivePlans() {
        return subscriptionRepo.findByStatus(SubscriptionStatus.ACTIVE).stream()
                .map(s -> new SubscriptionResponse(s.getId(), s.getName(), s.getPrice(), s.getDurationInDays(), s.getStatus(), s.getReleasedAt()))
                .toList();
    }


    @Override
    public SubscriptionResponse create(SubscriptionRequest req) {
        if (req.price() == null || req.price().compareTo(BigDecimal.ZERO) <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid price");
        if (req.durationInDays() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid duration");


        Subscription s = Subscription.builder()
                .name(req.name())
                .price(req.price())
                .durationInDays(req.durationInDays())
                .status(req.status() == null ? SubscriptionStatus.ACTIVE : req.status())
                .releasedAt(LocalDateTime.now())
                .build();
        s = subscriptionRepo.save(s);
        return new SubscriptionResponse(s.getId(), s.getName(), s.getPrice(), s.getDurationInDays(), s.getStatus(), s.getReleasedAt());
    }


    @Override
    public SubscriptionResponse update(String id, SubscriptionRequest req) {
        Subscription s = subscriptionRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        if (req.name() != null) s.setName(req.name());
        if (req.price() != null) s.setPrice(req.price());
        if (req.durationInDays() > 0) s.setDurationInDays(req.durationInDays());
        if (req.status() != null) s.setStatus(req.status());
        s = subscriptionRepo.save(s);
        return new SubscriptionResponse(s.getId(), s.getName(), s.getPrice(), s.getDurationInDays(), s.getStatus(), s.getReleasedAt());
    }


    @Override
    public void archive(String id) {
        Subscription s = subscriptionRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        s.setStatus(SubscriptionStatus.ARCHIVED);
        subscriptionRepo.save(s);
    }
}