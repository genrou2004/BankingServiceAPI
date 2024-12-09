package com.example.banking.controller;

import com.example.banking.model.Transaction;
import com.example.banking.service.TransactionService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    @Autowired
    private Validator validator;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<List<Transaction>> recordTransactions(@Valid @RequestBody List<Transaction> transactions) {
        logger.info("Request received to record transactions: {}", transactions);
        transactions.forEach(transaction -> {
            Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
            if (!violations.isEmpty()) {
                logger.error("Validation errors: {}", violations);
                throw new ConstraintViolationException(violations);
            }
        });
        List<Transaction> savedTransactions = transactionService.recordTransactions(transactions);
        logger.info("Transactions recorded successfully: {}", savedTransactions);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTransactions);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionsByAccountId(@PathVariable @NotBlank String accountId) {
        logger.info("Fetching transactions for account ID: {}", accountId);
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        logger.info("Transactions fetched for account ID {}: {}", accountId, transactions);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping
    public ResponseEntity<Page<Transaction>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Fetching all transactions, page: {}, size: {}", page, size);
        Page<Transaction> transactions = transactionService.getAllTransactions(page, size);
        logger.info("Fetched transactions: {}", transactions.getContent());
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Transaction>> getFilteredTransactions(
            @RequestParam @NotBlank String accountId,
            @RequestParam LocalDateTime from,
            @RequestParam LocalDateTime to) {
        logger.info("Fetching filtered transactions for account ID: {} from: {} to: {}", accountId, from, to);
        List<Transaction> transactions = transactionService.getFilteredTransactions(accountId, from, to);
        logger.info("Filtered transactions fetched: {}", transactions);
        return ResponseEntity.ok(transactions);
    }
}
