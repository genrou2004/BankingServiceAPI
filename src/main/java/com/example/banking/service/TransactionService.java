package com.example.banking.service;

import com.example.banking.model.Transaction;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    List<Transaction> recordTransactions(List<Transaction> transactions);
    List<Transaction> getTransactionsByAccountId(String accountId);

    Page<Transaction> getAllTransactions(int page, int size);

    List<Transaction> getFilteredTransactions(String accountId, LocalDateTime from, LocalDateTime to); // Filtered query
}