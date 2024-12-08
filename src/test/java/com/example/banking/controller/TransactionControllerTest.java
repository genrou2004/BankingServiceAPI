package com.example.banking.controller;

import com.example.banking.model.Transaction;
import com.example.banking.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    public TransactionControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRecordTransactions() {
        Transaction transaction = new Transaction();
        List<Transaction> transactions = Collections.singletonList(transaction);
        when(transactionService.recordTransactions(transactions)).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.recordTransactions(transactions);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(transactions, response.getBody());
        verify(transactionService, times(1)).recordTransactions(transactions);
    }

    @Test
    void testGetTransactionsByAccountId() {
        Transaction transaction = new Transaction();
        List<Transaction> transactions = Collections.singletonList(transaction);
        when(transactionService.getTransactionsByAccountId("1")).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getTransactionsByAccountId("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(transactions, response.getBody());
        verify(transactionService, times(1)).getTransactionsByAccountId("1");
    }

    @Test
    void testGetAllTransactions() {
        Page<Transaction> page = new PageImpl<>(Collections.singletonList(new Transaction()));
        when(transactionService.getAllTransactions(0, 10)).thenReturn(page);

        ResponseEntity<Page<Transaction>> response = transactionController.getAllTransactions(0, 10);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
        verify(transactionService, times(1)).getAllTransactions(0, 10);
    }

    @Test
    void testGetFilteredTransactions() {
        Transaction transaction = new Transaction();
        List<Transaction> transactions = Collections.singletonList(transaction);
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();

        when(transactionService.getFilteredTransactions("1", from, to)).thenReturn(transactions);

        ResponseEntity<List<Transaction>> response = transactionController.getFilteredTransactions("1", from, to);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(transactions, response.getBody());
        verify(transactionService, times(1)).getFilteredTransactions("1", from, to);
    }
}
