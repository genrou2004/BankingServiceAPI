package com.example.banking.service.impl;

import com.example.banking.event.EventPublisher;
import com.example.banking.model.Transaction;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.service.TransactionService;
import com.example.banking.util.EventUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EventPublisher eventPublisher;

    @Value("${kafka.topic.transaction-events}")
    private String transactionEventsTopic;

    @Override
    public List<Transaction> recordTransactions(List<Transaction> transactions) {
        List<Transaction> savedTransactions = transactionRepository.saveAll(transactions);
        eventPublisher.publishEvent(transactionEventsTopic, EventUtils.serializeEvent(savedTransactions));

        return savedTransactions;
    }

    @Override
    public List<Transaction> getTransactionsByAccountId(String accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    @Override
    public Page<Transaction> getAllTransactions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findAll(pageable);
    }

    @Override
    public List<Transaction> getFilteredTransactions(String accountId, LocalDateTime from, LocalDateTime to) {
        return transactionRepository.findByAccountIdAndTimestampBetween(accountId, from, to);
    }
}
