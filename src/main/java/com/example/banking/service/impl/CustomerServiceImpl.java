package com.example.banking.service.impl;

import com.example.banking.event.EventPublisher;
import com.example.banking.model.Customer;
import com.example.banking.repository.CustomerRepository;
import com.example.banking.service.CustomerService;
import com.example.banking.util.EventUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final EventPublisher eventPublisher;

    @Value("${kafka.topic.customer-events}")
    private String customerEventsTopic;

    public CustomerServiceImpl(CustomerRepository customerRepository, EventPublisher eventPublisher) {
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        logger.info("Creating customer: {}", customer);
        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer created successfully: {}", savedCustomer);

        // Publish customer creation event
        eventPublisher.publishEvent(customerEventsTopic, EventUtils.serializeEvent(savedCustomer));
        logger.info("Customer creation event published for customer ID: {}", savedCustomer.getId());

        return savedCustomer;
    }

    @Override
    public Customer getCustomerById(String id) {
        logger.info("Fetching customer with ID: {}", id);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Customer fetch failed: Customer ID {} not found", id);
                    return new IllegalArgumentException("Customer not found with ID: " + id);
                });
        logger.info("Customer fetched successfully: {}", customer);
        return customer;
    }

    @Override
    public Page<Customer> getAllCustomers(int page, int size) {
        logger.info("Fetching all customers, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Customer> customers = customerRepository.findAll(pageable);
        logger.info("Customers fetched successfully, page content: {}", customers.getContent());
        return customers;
    }
}
