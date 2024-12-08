package com.example.banking.service.impl;

import com.example.banking.event.EventPublisher;
import com.example.banking.model.Customer;
import com.example.banking.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(customerService, "customerEventsTopic", "customer-topic");
    }

    @Test
    void testCreateCustomer() {
        Customer customer = new Customer();
        customer.setId("1");

        when(customerRepository.save(customer)).thenReturn(customer);

        Customer savedCustomer = customerService.createCustomer(customer);

        assertNotNull(savedCustomer);
        assertEquals("1", savedCustomer.getId());
        verify(customerRepository, times(1)).save(customer);
        verify(eventPublisher, times(1)).publishEvent(anyString(), anyString());
    }

    @Test
    void testGetCustomerById() {
        Customer customer = new Customer();
        customer.setId("1");

        when(customerRepository.findById("1")).thenReturn(Optional.of(customer));

        Customer retrievedCustomer = customerService.getCustomerById("1");

        assertNotNull(retrievedCustomer);
        assertEquals("1", retrievedCustomer.getId());
        verify(customerRepository, times(1)).findById("1");
    }

    @Test
    void testGetAllCustomers() {
        Page<Customer> page = new PageImpl<>(Collections.singletonList(new Customer()));
        when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<Customer> customers = customerService.getAllCustomers(0, 10);

        assertNotNull(customers);
        assertEquals(1, customers.getTotalElements());
        verify(customerRepository, times(1)).findAll(any(Pageable.class));
    }
}
