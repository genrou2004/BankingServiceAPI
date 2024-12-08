package com.example.banking.model;

import com.example.banking.util.LocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "transactions")
public class Transaction {

    @Id
    @JsonIgnore
    private String id;
    @NotBlank(message = "Account ID must not be blank")
    private String accountId;
    @NotNull(message = "Transaction type must not be null")
    @Pattern(regexp = "DEPOSIT|WITHDRAW|TRANSFER", message = "Type must match one of the allowed types: [DEPOSIT, WITHDRAW, TRANSFER]")
    private TransactionType type;
    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;
    @NotNull(message = "Timestamp must not be null")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime timestamp;

    public Transaction() {
        this.id = UUID.randomUUID().toString();
    }

    public Transaction(String accountId, TransactionType type, BigDecimal amount) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER
    }
}