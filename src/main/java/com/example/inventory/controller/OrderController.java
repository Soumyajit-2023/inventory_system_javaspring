package com.example.inventory.controller;

import java.util.List; // Order entity represents order data

import org.springframework.beans.factory.annotation.Autowired; // Handles business logic for orders
import org.springframework.web.bind.annotation.GetMapping; // Dependency injection
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventory.entity.Order;
import com.example.inventory.service.OrderService;

/**
 * OrderController handles API requests related to orders, like placing a new order or fetching orders for a customer.
 *
 * Why use such a controller?
 * - Keeps "web" code (requests and responses) cleanly separated from business logic (handled in a service).
 * - Exposes endpoints for order-based operations to the outside world (frontend, Postman, etc).
 * 
 * Key annotations:
 * @RestController -- Returns JSON and not views/pages
 * @RequestMapping("/orders") -- All endpoints here start with /orders
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired // Dependency Injection: Spring will create and supply the required OrderService instance
    private OrderService orderService;

    /**
     * Nested static class used to receive complex data as a single object in POST requests.
     * 
     * Why use a request class (DTO)?
     * - Conveniently groups together all fields needed to place an order when receiving data from the frontend.
     * - Spring will automatically convert incoming JSON bodies to an instance of this class (using @RequestBody).
     *
     * What to take care:
     * - In production, consider validation (e.g. fields not null, values in valid ranges).
     * - In larger apps, you might want to pull this DTO out into its own file for clarity.
     */
    public static class PlaceOrderRequest {
        public Long customerId; // The ID of the customer placing the order
        public Long itemId;     // The inventory item being ordered
        public int quantity;    // The quantity of item to order

        // For simple cases, fields can be public, and Spring can still bind them from JSON.
        // Getters and setters are optional here but required if you use Java Bean standard.
        // In real-world projects, always prefer private fields with getters/setters and add validation.
    }

    /**
     * Handles POST requests to place a new order.
     * Example: POST /orders with JSON body {"customerId":1, "itemId":2, "quantity":10}
     * 
     * @param request will be auto-deserialized from the JSON body in the HTTP request
     * 
     * What @RequestBody does:
     * - Tells Spring to parse the HTTP request body as JSON and map it into a PlaceOrderRequest object.
     * 
     * Returns:
     * - The created Order object (as JSON).
     * 
     * Careful:
     * - No explicit error-handling: In practice, add checks for out-of-stock, non-existent customer/item, etc.
     */
    @PostMapping
    public Order placeOrder(@RequestBody PlaceOrderRequest request) {
        return orderService.placeOrder(request.customerId, request.itemId, request.quantity);
    }

    /**
     * Handles GET requests for all orders belonging to a customer.
     * Example: GET /orders/1 returns a list of orders for customer with ID 1
     * 
     * @param customerId Extracted directly from the URL (via @PathVariable)
     * 
     * Returns:
     * - List of Order objects for the specified customer
     * 
     * Careful:
     * - In production, handle the case where customerId does not exist and errors gracefully.
     */
    @GetMapping("/{customerId}")
    public List<Order> getOrdersForCustomer(@PathVariable Long customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }
}
