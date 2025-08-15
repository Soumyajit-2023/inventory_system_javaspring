package com.example.inventory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventory.entity.Customer;
import com.example.inventory.repository.CustomerRepository;

/**
 * CustomerService: Where business logic for customer operations lives.
 * 
 * Why use @Service?
 * - Marks this class as a "service" in the Spring application. It's where you put code that processes, transforms, or implements business rules.
 * - Best practice is to keep "controller" code focused on API input/output, and put all logic here.
 * 
 * What to take care about:
 * - Use services for input validation, permission checks, custom logic, etc.
 * - Don't directly access repositories in your controller—always go through the service.
 */
@Service
public class CustomerService {

    /**
     * Dependency injection of CustomerRepository.
     * - @Autowired tells Spring to give you the right repo instance.
     * - Lets you keep your code loosely coupled (you could swap in a mock for tests, for example).
     */
    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Returns a list of all customers in the system.
     * 
     * What to take care:
     * - In a real application, add pagination or filters to avoid loading too much data at once.
     */
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Returns a single customer by ID, or empty if not found.
     * 
     * @param id The customer's primary key from the database
     * @return Optional<Customer>: present if found, empty if not
     * 
     * What to take care:
     * - Always check for "not found" when using this—don't assume it always returns a customer!
     */
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    /**
     * Saves or updates a customer in the database.
     * 
     * @param customer The customer object to be saved (can be new or existing)
     * @return The saved customer (with ID if newly created)
     * 
     * What to take care:
     * - Add input validation (e.g., name not empty) in real-world apps!
     * - If customer already has an ID, this will update the existing row; otherwise, it creates a new one.
     */
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}
