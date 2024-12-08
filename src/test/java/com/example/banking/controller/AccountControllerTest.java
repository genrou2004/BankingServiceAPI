package com.example.banking.controller;

import com.example.banking.model.Account;
import com.example.banking.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    public AccountControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAccount() {
        Account account = new Account();
        account.setId("1");
        when(accountService.createAccount(account)).thenReturn(account);

        ResponseEntity<Account> response = accountController.createAccount(account);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(account, response.getBody());
        verify(accountService, times(1)).createAccount(account);
    }

    @Test
    void testTransfer() {
        String from = "1";
        String to = "2";
        BigDecimal amount = BigDecimal.valueOf(100);

        doNothing().when(accountService).transfer(from, to, amount);

        ResponseEntity<String> response = accountController.transfer(from, to, amount);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Transfer successful", response.getBody());
        verify(accountService, times(1)).transfer(from, to, amount);
    }

    @Test
    void testGetAccountById() {
        Account account = new Account();
        account.setId("1");
        when(accountService.getAccountById("1")).thenReturn(account);

        ResponseEntity<Account> response = accountController.getAccountById("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(account, response.getBody());
        verify(accountService, times(1)).getAccountById("1");
    }

    @Test
    void testGetAllAccounts() {
        Page<Account> page = new PageImpl<>(Collections.singletonList(new Account()));
        when(accountService.getAllAccounts(0, 10)).thenReturn(page);

        ResponseEntity<Page<Account>> response = accountController.getAllAccounts(0, 10);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
        verify(accountService, times(1)).getAllAccounts(0, 10);
    }
}
