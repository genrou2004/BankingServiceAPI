package com.example.banking.controller;

import com.example.banking.model.Customer;
import com.example.banking.service.CustomerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomers(@Valid @RequestBody Customer customer) {
        logger.info("Request received to create customer: {}", customer);
        Customer savedCustomer = customerService.createCustomer(customer);
        logger.info("Customer created successfully: {}", savedCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }

    @GetMapping
    public ResponseEntity<Page<Customer>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Fetching all customers, page: {}, size: {}", page, size);
        Page<Customer> customers = customerService.getAllCustomers(page, size);
        logger.info("Customers fetched successfully: {}", customers.getContent());
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable @NotBlank String id) {
        logger.info("Fetching customer with ID: {}", id);
        Customer customer = customerService.getCustomerById(id);
        logger.info("Customer fetched successfully: {}", customer);
        return ResponseEntity.ok(customer);
    }
}
