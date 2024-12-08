package com.example.banking.service.impl;

import com.example.banking.event.EventPublisher;
import com.example.banking.event.TransferEvent;
import com.example.banking.model.Account;
import com.example.banking.model.Transaction;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.service.AccountService;
import com.example.banking.util.EventUtils;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final EventPublisher eventPublisher;

    @Value("${kafka.topic.account-events}")
    private String accountEventsTopic;

    @Value("${kafka.topic.transfer-events}")
    private String transferEventsTopic;

    public AccountServiceImpl(AccountRepository accountRepository,
                              TransactionRepository transactionRepository,
                              EventPublisher eventPublisher) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Account createAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null");
        }

        Account savedAccount = accountRepository.save(account);

        // Publish account creation event
        eventPublisher.publishEvent(accountEventsTopic, EventUtils.serializeEvent(savedAccount));

        return savedAccount;
    }

    @Transactional
    @Override
    public void transfer(@NotNull String fromAccountId, @NotNull String toAccountId, @NotNull BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid from account ID: " + fromAccountId));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid to account ID: " + toAccountId));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance in account: " + fromAccountId);
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction debitTransaction = new Transaction(fromAccountId, Transaction.TransactionType.TRANSFER, amount.negate());
        Transaction creditTransaction = new Transaction(toAccountId, Transaction.TransactionType.TRANSFER, amount);

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        // Publish transfer event
        TransferEvent transferEvent = new TransferEvent(fromAccountId, toAccountId, amount);
        eventPublisher.publishEvent(transferEventsTopic, EventUtils.serializeEvent(transferEvent));
    }

    @Override
    public List<Account> getAccountsByCustomerId(@NotNull String customerId) {
        return accountRepository.findByCustomerId(customerId);
    }

    @Override
    public Account getAccountById(@NotNull String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + id));
    }

    @Override
    public Page<Account> getAllAccounts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return accountRepository.findAll(pageable);
    }
}
