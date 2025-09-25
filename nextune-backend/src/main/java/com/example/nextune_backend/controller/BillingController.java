package com.example.nextune_backend.controller;

import com.example.nextune_backend.dto.request.PurchaseRequest;
import com.example.nextune_backend.dto.response.PurchaseInitResponse;
import com.example.nextune_backend.service.BillingService;
import com.example.nextune_backend.utility.UserUtility;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
public class BillingController {
    private final BillingService billingService;
    private final UserUtility userUtil;

    @PostMapping("/purchase")
    public PurchaseInitResponse purchase(@RequestBody PurchaseRequest req,
                                         HttpServletRequest servletReq) {
        // chỉ nên lấy userId từ UserUtility / SecurityContext
        String userId = userUtil.getCurrentUserId();
        return billingService.initPurchase(userId, req);
    }

    // VNPay IPN: nên mở cả GET & POST
    @GetMapping("/vnpay/ipn")
    public ResponseEntity<String> vnpayIpnGet(@RequestParam Map<String, String> allParams) {
        // debug tạm thời
        System.out.println("VNPay IPN (GET): " + allParams);
        billingService.handleVNPayIPN(allParams);
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/vnpay/ipn")
    public ResponseEntity<String> vnpayIpnPost(@RequestParam Map<String, String> allParams) {
        System.out.println("VNPay IPN (POST): " + allParams);
        billingService.handleVNPayIPN(allParams);
        return ResponseEntity.ok("OK");
    }
}
