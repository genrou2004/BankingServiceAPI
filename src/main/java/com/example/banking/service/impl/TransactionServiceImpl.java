package com.example.banking.service.impl;

import com.example.banking.event.EventPublisher;
import com.example.banking.model.Transaction;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.service.TransactionService;
import com.example.banking.util.EventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final EventPublisher eventPublisher;

    @Value("${kafka.topic.transaction-events}")
    private String transactionEventsTopic;

    public TransactionServiceImpl(TransactionRepository transactionRepository, EventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<Transaction> recordTransactions(List<Transaction> transactions) {
        logger.info("Recording transactions: {}", transactions);
        List<Transaction> savedTransactions = transactionRepository.saveAll(transactions);
        logger.info("Transactions recorded successfully: {}", savedTransactions);

        // Publish transaction events
        eventPublisher.publishEvent(transactionEventsTopic, EventUtils.serializeEvent(savedTransactions));
        logger.info("Transaction events published successfully for transactions: {}", savedTransactions);

        return savedTransactions;
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(String accountId) {
        logger.info("Fetching transactions for account ID: {}", accountId);
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        logger.info("Transactions fetched for account ID {}: {}", accountId, transactions);
        return transactions;
    }

    @Override
    public Page<Transaction> getAllTransactions(int page, int size) {
        logger.info("Fetching all transactions, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        logger.info("Fetched transactions: {}", transactions.getContent());
        return transactions;
    }

    @Override
    public List<Transaction> getFilteredTransactions(String accountId, LocalDateTime from, LocalDateTime to) {
        logger.info("Fetching filtered transactions for account ID: {} from: {} to: {}", accountId, from, to);
        List<Transaction> transactions = transactionRepository.findByAccountIdAndTimestampBetween(accountId, from, to);
        logger.info("Filtered transactions fetched: {}", transactions);
        return transactions;
    }
}
