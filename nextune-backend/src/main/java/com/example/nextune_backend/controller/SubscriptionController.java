package com.example.nextune_backend.controller;



import com.example.nextune_backend.dto.request.SubscriptionRequest;
import com.example.nextune_backend.dto.response.SubscriptionResponse;
import com.example.nextune_backend.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;


    @GetMapping
    public List<SubscriptionResponse> list() {
        return subscriptionService.listActivePlans();
    }


    // Admin only
    @PostMapping
    public SubscriptionResponse create(@RequestBody SubscriptionRequest req) {
        return subscriptionService.create(req);
    }


    // Admin only
    @PutMapping("/{id}")
    public SubscriptionResponse update(@PathVariable String id, @RequestBody SubscriptionRequest req) {
        return subscriptionService.update(id, req);
    }


    // Admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archive(@PathVariable String id) {
        subscriptionService.archive(id);
        return ResponseEntity.noContent().build();
    }
}