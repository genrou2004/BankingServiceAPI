package com.example.banking.controller;

import com.example.banking.model.Customer;
import com.example.banking.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    public CustomerControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCustomers() {
        Customer customer = new Customer();
        customer.setId("1");
        when(customerService.createCustomer(customer)).thenReturn(customer);

        ResponseEntity<Customer> response = customerController.createCustomers(customer);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(customer, response.getBody());
        verify(customerService, times(1)).createCustomer(customer);
    }

    @Test
    void testGetAllCustomers() {
        Page<Customer> page = new PageImpl<>(Collections.singletonList(new Customer()));
        when(customerService.getAllCustomers(0, 10)).thenReturn(page);

        ResponseEntity<Page<Customer>> response = customerController.getAllCustomers(0, 10);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(page, response.getBody());
        verify(customerService, times(1)).getAllCustomers(0, 10);
    }

    @Test
    void testGetCustomerById() {
        Customer customer = new Customer();
        customer.setId("1");
        when(customerService.getCustomerById("1")).thenReturn(customer);

        ResponseEntity<Customer> response = customerController.getCustomerById("1");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(customer, response.getBody());
        verify(customerService, times(1)).getCustomerById("1");
    }
}
