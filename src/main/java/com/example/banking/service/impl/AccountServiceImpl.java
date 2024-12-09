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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

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
        logger.info("Creating account: {}", account);
        if (account == null) {
            logger.error("Account creation failed: Account is null");
            throw new IllegalArgumentException("Account cannot be null");
        }

        Account savedAccount = accountRepository.save(account);
        logger.info("Account created successfully: {}", savedAccount);

        // Publish account creation event
        eventPublisher.publishEvent(accountEventsTopic, EventUtils.serializeEvent(savedAccount));
        logger.info("Account creation event published for account ID: {}", savedAccount.getId());

        return savedAccount;
    }

    @Transactional
    @Override
    public void transfer(@NotNull String fromAccountId, @NotNull String toAccountId, @NotNull BigDecimal amount) {
        logger.info("Initiating transfer from account {} to account {} with amount {}", fromAccountId, toAccountId, amount);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Transfer failed: Invalid transfer amount {}", amount);
            throw new IllegalArgumentException("Transfer amount must be greater than zero");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> {
                    logger.error("Transfer failed: From account ID {} not found", fromAccountId);
                    return new IllegalArgumentException("Invalid from account ID: " + fromAccountId);
                });

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> {
                    logger.error("Transfer failed: To account ID {} not found", toAccountId);
                    return new IllegalArgumentException("Invalid to account ID: " + toAccountId);
                });

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            logger.error("Transfer failed: Insufficient balance in account ID {}", fromAccountId);
            throw new IllegalArgumentException("Insufficient balance in account: " + fromAccountId);
        }

        // Update balances
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        logger.info("Transfer successful: {} from account {} to account {}", amount, fromAccountId, toAccountId);

        // Record transactions
        Transaction debitTransaction = new Transaction(fromAccountId, Transaction.TransactionType.TRANSFER, amount.negate());
        Transaction creditTransaction = new Transaction(toAccountId, Transaction.TransactionType.TRANSFER, amount);
        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        logger.info("Transactions recorded for transfer: Debit={}, Credit={}", debitTransaction, creditTransaction);

        // Publish transfer event
        TransferEvent transferEvent = new TransferEvent(fromAccountId, toAccountId, amount);
        eventPublisher.publishEvent(transferEventsTopic, EventUtils.serializeEvent(transferEvent));
        logger.info("Transfer event published successfully");
    }

    @Override
    public List<Account> getAccountsByCustomerId(@NotNull String customerId) {
        logger.info("Fetching accounts for customer ID: {}", customerId);
        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        logger.info("Accounts fetched for customer ID {}: {}", customerId, accounts);
        return accounts;
    }

    @Override
    public Account getAccountById(@NotNull String id) {
        logger.info("Fetching account with ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Account fetch failed: Account ID {} not found", id);
                    return new IllegalArgumentException("Account not found with ID: " + id);
                });
        logger.info("Account fetched successfully: {}", account);
        return account;
    }

    @Override
    public Page<Account> getAllAccounts(int page, int size) {
        logger.info("Fetching all accounts, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> accounts = accountRepository.findAll(pageable);
        logger.info("Accounts fetched successfully, page content: {}", accounts.getContent());
        return accounts;
    }
}
