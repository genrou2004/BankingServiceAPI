package com.example.banking.service.impl;

import com.example.banking.event.EventPublisher;
import com.example.banking.model.Account;
import com.example.banking.model.Transaction;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;
import com.example.banking.util.EventUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private AccountServiceImpl accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Inject mock values for @Value properties
        ReflectionTestUtils.setField(accountService, "accountEventsTopic", "account-topic");
        ReflectionTestUtils.setField(accountService, "transferEventsTopic", "transfer-topic");
    }

    @Test
    void testCreateAccount() {
        Account account = new Account();
        account.setId("1");

        when(accountRepository.save(account)).thenReturn(account);

        Account savedAccount = accountService.createAccount(account);

        assertNotNull(savedAccount);
        assertEquals("1", savedAccount.getId());
        verify(accountRepository, times(1)).save(account);
        verify(eventPublisher, times(1)).publishEvent(anyString(), anyString());
    }

    @Test
    void testTransfer() {
        Account fromAccount = new Account();
        fromAccount.setId("1");
        fromAccount.setBalance(BigDecimal.valueOf(200));

        Account toAccount = new Account();
        toAccount.setId("2");
        toAccount.setBalance(BigDecimal.valueOf(100));

        when(accountRepository.findById("1")).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById("2")).thenReturn(Optional.of(toAccount));

        accountService.transfer("1", "2", BigDecimal.valueOf(50));

        assertEquals(BigDecimal.valueOf(150), fromAccount.getBalance());
        assertEquals(BigDecimal.valueOf(150), toAccount.getBalance());

        verify(accountRepository, times(2)).save(any(Account.class));
        verify(transactionRepository, times(2)).save(any(Transaction.class));
        verify(eventPublisher, times(1)).publishEvent(anyString(), anyString());
    }

    @Test
    void testGetAccountById() {
        Account account = new Account();
        account.setId("1");

        when(accountRepository.findById("1")).thenReturn(Optional.of(account));

        Account retrievedAccount = accountService.getAccountById("1");

        assertNotNull(retrievedAccount);
        assertEquals("1", retrievedAccount.getId());
        verify(accountRepository, times(1)).findById("1");
    }

    @Test
    void testGetAllAccounts() {
        Page<Account> page = new PageImpl<>(Collections.singletonList(new Account()));
        when(accountRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Account> accounts = accountService.getAllAccounts(0, 10);

        assertNotNull(accounts);
        assertEquals(1, accounts.getTotalElements());
        verify(accountRepository, times(1)).findAll(any(Pageable.class));
    }
}
