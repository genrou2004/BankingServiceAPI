package com.example.banking.controller;

import com.example.banking.model.Account;
import com.example.banking.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account) {
        Account savedAccount = accountService.createAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAccount);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(
            @RequestParam @NotBlank String fromAccountId,
            @RequestParam @NotBlank String toAccountId,
            @RequestParam @Min(1) BigDecimal amount) {
        accountService.transfer(fromAccountId, toAccountId, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body("Transfer successful");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable @NotBlank String id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping
    public ResponseEntity<Page<Account>> getAllAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Account> accounts = accountService.getAllAccounts(page, size);
        return ResponseEntity.ok(accounts);
    }

}