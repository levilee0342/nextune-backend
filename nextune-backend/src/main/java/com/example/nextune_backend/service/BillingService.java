package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.PaymentWebhookPayload;
import com.example.nextune_backend.dto.request.PurchaseRequest;
import com.example.nextune_backend.dto.response.PurchaseInitResponse;
import com.example.nextune_backend.entity.Subscription;
import com.example.nextune_backend.entity.User;

public interface BillingService {
    PurchaseInitResponse initPurchase(String userId, PurchaseRequest req);
    void handleVNPayIPN(java.util.Map<String, String> ipnParams); // raw params from IPN
}