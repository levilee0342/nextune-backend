package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.PurchaseRequest;
import com.example.nextune_backend.dto.response.PurchaseInitResponse;
import com.example.nextune_backend.entity.Subscription;
import com.example.nextune_backend.entity.Transaction;
import com.example.nextune_backend.entity.TransactionId;
import com.example.nextune_backend.entity.User;
import com.example.nextune_backend.entity.enums.PaymentMethod;
import com.example.nextune_backend.entity.enums.SubscriptionStatus;
import com.example.nextune_backend.entity.enums.TransactionStatus;
import com.example.nextune_backend.repository.SubscriptionRepository;
import com.example.nextune_backend.repository.TransactionRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.BillingService;
import com.example.nextune_backend.service.VNPayGatewayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BillingServiceImpl implements BillingService {
    private final TransactionRepository txRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final UserRepository userRepo;
    private final VNPayGatewayService vnPayGateway;


    private static final BigDecimal VAT_RATE = new BigDecimal("0.00"); // 10% VAT (example)


    @Transactional
    @Override
    public PurchaseInitResponse initPurchase(String userId, PurchaseRequest req) {
        if (req.idempotencyKey() == null || req.idempotencyKey().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing idempotencyKey");
        txRepo.findByIdempotencyKey(req.idempotencyKey()).ifPresent(t -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Duplicate request");
        });


        Subscription plan = subscriptionRepo.findById(req.subscriptionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subscription not found"));
        if (plan.getStatus() != SubscriptionStatus.ACTIVE)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Subscription inactive");


        User user = userRepo.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        BigDecimal tax = plan.getPrice().multiply(VAT_RATE).setScale(0, BigDecimal.ROUND_HALF_UP); // VND round
        BigDecimal total = plan.getPrice().add(tax);


// For VNPay we need a unique vnp_TxnRef (max 100 chars). Use idempotencyKey or compose one.
        String txnRef = req.idempotencyKey();


        TransactionId id = TransactionId.builder()
                .subscriptionId(plan.getId())
                .userId(user.getId())
                .build();


        Transaction tx = Transaction.builder()
                .id(id)
                .subscription(plan)
                .user(user)
                .paymentMethod(PaymentMethod.VNPAY)
                .totalPrice(plan.getPrice())
                .totalTax(tax)
                .status(TransactionStatus.PENDING)
                .gatewayTransactionId(txnRef)
                .idempotencyKey(req.idempotencyKey())
                .build();
        txRepo.save(tx);


        String orderInfo = "Subscription " + plan.getName() + " for user " + user.getEmail();
        String clientIp = "127.0.0.1"; // you may pass from controller via HttpServletRequest
        String checkoutUrl = vnPayGateway.createPaymentUrl(orderInfo, txnRef, total, clientIp);


        return new PurchaseInitResponse(plan.getId(), user.getId(), checkoutUrl, txnRef, req.idempotencyKey());
    }


    @Transactional
    @Override
    public void handleVNPayIPN(Map<String, String> ipnParams) {
    // 1) Verify signature
        boolean ok = vnPayGateway.verifyIPNSignature(ipnParams);
        if (!ok) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid signature");


        String vnpTxnRef = ipnParams.get("vnp_TxnRef");
        String rspCode = ipnParams.get("vnp_ResponseCode");
        String txnStatus = ipnParams.get("vnp_TransactionStatus");


    // 2) Find transaction by gateway ref
        Transaction tx = txRepo.findByGatewayTransactionId(vnpTxnRef)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));


        if (Objects.equals(rspCode, "00") && Objects.equals(txnStatus, "00")) {
    // Success
            tx.setStatus(TransactionStatus.PAID);
            tx.setPaidAt(LocalDateTime.now());
            txRepo.save(tx);


    // Apply premium
            User user = tx.getUser();
            Subscription plan = tx.getSubscription();
            LocalDateTime base = (user.getPremiumDueDate() != null && user.getPremiumDueDate().isAfter(LocalDateTime.now()))
                    ? user.getPremiumDueDate() : LocalDateTime.now();
            user.setIsPremium(true);
            user.setPremiumDueDate(base.plusDays(plan.getDurationInDays()));
            userRepo.save(user);


        } else {
    // Fail/Cancel
            tx.setStatus(TransactionStatus.FAILED);
            txRepo.save(tx);
        }
    }
}