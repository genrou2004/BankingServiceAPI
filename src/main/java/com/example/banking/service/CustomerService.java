package com.example.banking.service;

import com.example.banking.model.Customer;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    Page<Customer> getAllCustomers(int page, int size);
    Customer getCustomerById(String id);
}