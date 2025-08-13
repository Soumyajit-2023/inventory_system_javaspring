package com.example.inventory.service;

import com.example.inventory.entity.Customer;
import com.example.inventory.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void testGetAllCustomers_returnsList() {
        Customer c1 = new Customer();
        c1.setName("Alice");
        Customer c2 = new Customer();
        c2.setName("Bob");
        List<Customer> list = Arrays.asList(c1, c2);

        when(customerRepository.findAll()).thenReturn(list);

        List<Customer> result = customerService.getAllCustomers();
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
    }

    @Test
    void testGetCustomerById_found() {
        Customer c = new Customer();
        c.setName("Test");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        Optional<Customer> result = customerService.getCustomerById(1L);
        assertTrue(result.isPresent());
        assertEquals("Test", result.get().getName());
    }

    @Test
    void testGetCustomerById_notFound() {
        when(customerRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Customer> result = customerService.getCustomerById(2L);
        assertFalse(result.isPresent());
    }

    @Test
    void testSaveCustomer_savesAndReturnsEntity() {
        Customer c = new Customer();
        c.setName("New Cust");
        when(customerRepository.save(any(Customer.class))).thenReturn(c);

        Customer saved = customerService.saveCustomer(c);
        assertEquals("New Cust", saved.getName());
        verify(customerRepository).save(c);
    }
}
