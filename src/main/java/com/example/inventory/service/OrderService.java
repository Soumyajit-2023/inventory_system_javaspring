package com.example.inventory.service;

import com.example.inventory.entity.Customer;
import com.example.inventory.entity.InventoryItem;
import com.example.inventory.entity.Order;
import com.example.inventory.repository.CustomerRepository;
import com.example.inventory.repository.InventoryItemRepository;
import com.example.inventory.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    // Place an order and return the created Order entity
    public Order placeOrder(Long customerId, Long itemId, int quantity) {
        if (quantity <= 0) {
            // Reject the order if quantity is invalid
            Optional<Customer> customerOpt = customerRepository.findById(customerId);
            Optional<InventoryItem> itemOpt = inventoryItemRepository.findById(itemId);
            Order rejectedOrder = new Order(
                    customerOpt.orElse(null),
                    itemOpt.orElse(null),
                    quantity,
                    "REJECTED");
            return orderRepository.save(rejectedOrder);
        }

        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        Optional<InventoryItem> itemOpt = inventoryItemRepository.findById(itemId);

        if (customerOpt.isEmpty() || itemOpt.isEmpty()) {
            // Return order with status REJECTED if either does not exist
            Order rejectedOrder = new Order(
                    customerOpt.orElse(null),
                    itemOpt.orElse(null),
                    quantity,
                    "REJECTED");
            return orderRepository.save(rejectedOrder);
        }

        InventoryItem item = itemOpt.get();
        boolean enoughStock = item.getQuantity() >= quantity;

        String status;
        if (enoughStock) {
            inventoryService.decreaseStock(itemId, quantity);
            status = "PLACED";
        } else {
            status = "REJECTED";
        }

        Order order = new Order(customerOpt.get(), item, quantity, status);
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}
