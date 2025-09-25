package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.Transaction;
import com.example.nextune_backend.entity.TransactionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, TransactionId> {
    List<Transaction> findByUser_IdOrderByPaidAtDesc(String userId);
    Optional<Transaction> findByGatewayTransactionId(String gatewayTxId);
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
}