package com.example.inventory.controller;

import com.example.inventory.entity.Order;
import com.example.inventory.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    public static class PlaceOrderRequest {
        public Long customerId;
        public Long itemId;
        public int quantity;

        // getters and setters not strictly needed for @RequestBody in record style
    }

    @PostMapping
    public Order placeOrder(@RequestBody PlaceOrderRequest request) {
        return orderService.placeOrder(request.customerId, request.itemId, request.quantity);
    }

    @GetMapping("/{customerId}")
    public List<Order> getOrdersForCustomer(@PathVariable Long customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }
}
