package com.example.banking.service.impl;

import com.example.banking.event.EventPublisher;
import com.example.banking.model.Transaction;
import com.example.banking.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(transactionService, "transactionEventsTopic", "transaction-topic");
    }

    @Test
    void testRecordTransactions() {
        Transaction transaction = new Transaction();
        List<Transaction> transactions = Collections.singletonList(transaction);

        when(transactionRepository.saveAll(transactions)).thenReturn(transactions);

        List<Transaction> savedTransactions = transactionService.recordTransactions(transactions);

        assertNotNull(savedTransactions);
        assertEquals(1, savedTransactions.size());
        verify(transactionRepository, times(1)).saveAll(transactions);
        verify(eventPublisher, times(1)).publishEvent(anyString(), anyString());
    }

    @Test
    void testGetTransactionsByAccountId() {
        Transaction transaction = new Transaction();
        List<Transaction> transactions = Collections.singletonList(transaction);

        when(transactionRepository.findByAccountId("1")).thenReturn(transactions);

        List<Transaction> retrievedTransactions = transactionService.getTransactionsByAccountId("1");

        assertNotNull(retrievedTransactions);
        assertEquals(1, retrievedTransactions.size());
        verify(transactionRepository, times(1)).findByAccountId("1");
    }

    @Test
    void testGetFilteredTransactions() {
        Transaction transaction = new Transaction();
        List<Transaction> transactions = Collections.singletonList(transaction);
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();

        when(transactionRepository.findByAccountIdAndTimestampBetween("1", from, to)).thenReturn(transactions);

        List<Transaction> retrievedTransactions = transactionService.getFilteredTransactions("1", from, to);

        assertNotNull(retrievedTransactions);
        assertEquals(1, retrievedTransactions.size());
        verify(transactionRepository, times(1)).findByAccountIdAndTimestampBetween("1", from, to);
    }

    @Test
    void testGetAllTransactions() {
        Page<Transaction> page = new PageImpl<>(Collections.singletonList(new Transaction()));
        when(transactionRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Transaction> transactions = transactionService.getAllTransactions(0, 10);

        assertNotNull(transactions);
        assertEquals(1, transactions.getTotalElements());
        verify(transactionRepository, times(1)).findAll(any(Pageable.class));
    }
}
