package com.example.banking.controller;

import com.example.banking.model.Account;
import com.example.banking.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account) {
        logger.info("Request received to create account: {}", account);
        Account savedAccount = accountService.createAccount(account);
        logger.info("Account created successfully: {}", savedAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAccount);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @RequestParam @NotBlank String fromAccountId,
            @RequestParam @NotBlank String toAccountId,
            @RequestParam @Min(1) BigDecimal amount) {
        logger.info("Request received to transfer from account {} to account {} with amount {}", fromAccountId, toAccountId, amount);
        accountService.transfer(fromAccountId, toAccountId, amount);
        logger.info("Transfer completed successfully from {} to {} with amount {}", fromAccountId, toAccountId, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body("Transfer successful");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable @NotBlank String id) {
        logger.info("Fetching account with ID: {}", id);
        Account account = accountService.getAccountById(id);
        logger.info("Account fetched successfully: {}", account);
        return ResponseEntity.ok(account);
    }

    @GetMapping
    public ResponseEntity<Page<Account>> getAllAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Fetching all accounts, page: {}, size: {}", page, size);
        Page<Account> accounts = accountService.getAllAccounts(page, size);
        logger.info("Accounts fetched successfully: {}", accounts.getContent());
        return ResponseEntity.ok(accounts);
    }
}
