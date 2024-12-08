package com.example.banking.repository;

import com.example.banking.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findByAccountId(String accountId);

    Page<Transaction> findAll(Pageable pageable);

    List<Transaction> findByAccountIdAndTimestampBetween(String accountId, LocalDateTime from, LocalDateTime to);
}