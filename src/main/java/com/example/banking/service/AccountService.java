package com.example.banking.service;

import com.example.banking.model.Account;
import com.example.banking.model.Transaction;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    Account createAccount(Account account);

    void transfer(String fromAccountId, String toAccountId, BigDecimal amount);

    List<Account> getAccountsByCustomerId(@NotNull String customerId);

    Account getAccountById(String id);

    Page<Account> getAllAccounts(int page, int size);
}