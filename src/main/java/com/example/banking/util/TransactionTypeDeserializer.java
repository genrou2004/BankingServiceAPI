package com.example.banking.util;

import com.example.banking.model.Transaction;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public class TransactionTypeDeserializer extends JsonDeserializer<Transaction.TransactionType> {

    @Override
    public Transaction.TransactionType deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        String value = parser.getText().trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("Transaction type must not be blank");
        }
        try {
            return Transaction.TransactionType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid transaction type: " + value);
        }
    }
}
