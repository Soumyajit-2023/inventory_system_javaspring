package com.example.inventory.controller;

import java.util.List; // Import your Customer entity class (data structure for customer info)

import org.springframework.beans.factory.annotation.Autowired; // Import the service layer for all business logic related to customers
import org.springframework.web.bind.annotation.GetMapping; // Used to inject (auto-wire) needed dependencies
import org.springframework.web.bind.annotation.PostMapping; // Import core REST API annotations: @RestController, @RequestMapping, etc.
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventory.entity.Customer;
import com.example.inventory.service.CustomerService;

/**
 * CustomerController handles all HTTP requests related to customer management.
 * 
 * Why use a Controller?
 * - This acts as the "gatekeeper" for your app’s API—accepting requests, calling the right business logic, and formatting responses.
 * 
 * What is @RestController?
 * - Tells Spring this class is a RESTful API Controller: it automatically converts return values (like Customer objects) to JSON for the frontend/app.
 * 
 * What should you take care?
 * - Put *only* request-handling logic here, not business rules or database queries (use Service for those!).
 */
@RestController
@RequestMapping("/customers") // Every URL handled here will start with /customers (like /customers, /customers/123)
public class CustomerController {

    @Autowired // This tells Spring to "inject" (supply) the right CustomerService instance here—Dependency Injection best-practice!
    private CustomerService customerService;

    /**
     * Handles GET requests for all customers.
     * Example: GET /customers
     * 
     * Why use @GetMapping?
     * - Maps HTTP GET (read) requests to this method.
     * 
     * What to take care:
     * - Should return all customers in the system.
     * - No request parameters needed here.
     */
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers(); // Delegates to service layer for fetching data
    }

    /**
     * Handles POST requests to add a new customer.
     * Example: POST /customers with Customer data in JSON payload
     * 
     * Why use @PostMapping?
     * - Maps HTTP POST (create) requests to this method.
     * 
     * What does @RequestBody do?
     * - Automatically converts incoming JSON in the request into a Customer Java object.
     * 
     * What to take care:
     * - Make sure Customer fields are correctly validated for real apps (input validation!).
     */
    @PostMapping
    public Customer addCustomer(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer); // Delegates work of saving to the service layer
    }
}
