package com.example.banking.controller;

import com.example.banking.model.Transaction;
import com.example.banking.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRecordTransactions_ValidTransactions() throws Exception {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setAccountId("123");
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setTimestamp(LocalDateTime.now());

        List<Transaction> transactions = Collections.singletonList(transaction);

        when(transactionService.recordTransactions(any())).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactions)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].accountId").value("123"))
                .andExpect(jsonPath("$[0].amount").value(100));

        verify(transactionService, times(1)).recordTransactions(any());
    }

    @Test
    void testRecordTransactions_InvalidTransaction() throws Exception {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setAccountId(""); // Invalid accountId
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setTimestamp(LocalDateTime.now());

        List<Transaction> transactions = Collections.singletonList(transaction);

        // Act & Assert
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactions)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.accountId").exists()); // Validation error expected

        verify(transactionService, never()).recordTransactions(any());
    }

    @Test
    void testGetTransactionsByAccountId() throws Exception {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setAccountId("123");
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setTimestamp(LocalDateTime.now());

        List<Transaction> transactions = Collections.singletonList(transaction);

        when(transactionService.getTransactionsByAccountId("123")).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/transactions/123")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value("123"))
                .andExpect(jsonPath("$[0].amount").value(100));

        verify(transactionService, times(1)).getTransactionsByAccountId("123");
    }

    @Test
    void testGetAllTransactions() throws Exception {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setAccountId("123");
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setTimestamp(LocalDateTime.now());

        Page<Transaction> transactionPage = new PageImpl<>(Collections.singletonList(transaction));

        when(transactionService.getAllTransactions(0, 10)).thenReturn(transactionPage);

        // Act & Assert
        mockMvc.perform(get("/transactions")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].accountId").value("123"))
                .andExpect(jsonPath("$.content[0].amount").value(100));

        verify(transactionService, times(1)).getAllTransactions(0, 10);
    }

    @Test
    void testGetFilteredTransactions() throws Exception {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setAccountId("123");
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.valueOf(100));
        transaction.setTimestamp(LocalDateTime.now());

        List<Transaction> transactions = Collections.singletonList(transaction);

        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now();

        when(transactionService.getFilteredTransactions("123", from, to)).thenReturn(transactions);

        // Act & Assert
        mockMvc.perform(get("/transactions/filter")
                        .param("accountId", "123")
                        .param("from", from.toString())
                        .param("to", to.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value("123"))
                .andExpect(jsonPath("$[0].amount").value(100));

        verify(transactionService, times(1)).getFilteredTransactions("123", from, to);
    }
}
