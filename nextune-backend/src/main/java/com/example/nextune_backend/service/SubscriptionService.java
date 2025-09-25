package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.SubscriptionRequest;
import com.example.nextune_backend.dto.response.SubscriptionResponse;

import java.util.List;

public interface SubscriptionService {
    List<SubscriptionResponse> listActivePlans();
    SubscriptionResponse create(SubscriptionRequest req);     // admin
    SubscriptionResponse update(String id, SubscriptionRequest req); // admin
    void archive(String id); // admin
}