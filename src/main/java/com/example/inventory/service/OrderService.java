package com.example.inventory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventory.entity.Customer;
import com.example.inventory.entity.InventoryItem;
import com.example.inventory.entity.Order;
import com.example.inventory.repository.CustomerRepository;
import com.example.inventory.repository.InventoryItemRepository;
import com.example.inventory.repository.OrderRepository;

/**
 * OrderService: This is the core of "order" business logic.
 * 
 * Why use @Service?
 * - Encapsulates all application/order-specific rules and workflows apart from the controllers and data access (repositories).
 * - Promotes maintainability by centralizing logic here.
 * 
 * What to take care:
 * - Inject other services (like InventoryService) when business rules span across domain models (updating inventory in an order, etc).
 */
@Service
public class OrderService {

    @Autowired // Repository to save/fetch Order entities
    private OrderRepository orderRepository;

    @Autowired // Repository to fetch Customer entity (ensures customer exists before placing order)
    private CustomerRepository customerRepository;

    @Autowired // Use business logic for inventory, not just DB (decreases stock, etc.)
    private InventoryService inventoryService;

    @Autowired // Sometimes also use repo directly for raw item lookups
    private InventoryItemRepository inventoryItemRepository;

    /**
     * Places an order if valid, otherwise records as "REJECTED".
     * 
     * @param customerId the ID of the customer placing the order
     * @param itemId     the ID of the item being ordered
     * @param quantity   how many units of the item to order
     * @return the Order object as persisted (either "PLACED" or "REJECTED")
     * 
     * Step-by-step business logic:
     * 1. If quantity is invalid (<=0), create and save a "REJECTED" order record.
     * 2. Fetch customer and item by ID; if either doesn’t exist, reject order.
     * 3. Check available item stock:
     *    - If enough, call InventoryService to decrease the stock & mark order as "PLACED".
     *    - If not enough stock, mark order as "REJECTED".
     * 4. Save order (always saved, for tracking rejected orders/audit trails).
     * 
     * Careful:
     * - Always defend against nulls and not-found cases!
     * - In real-world, consider transactionality; use @Transactional if you want stock/order all to succeed/fail together.
     * - In robust systems, you'd log reasons for rejection and notify customer.
     */
    public Order placeOrder(Long customerId, Long itemId, int quantity) {
        if (quantity <= 0) {
            // Reject the order if quantity is invalid (negative or zero)
            Optional<Customer> customerOpt = customerRepository.findById(customerId);
            Optional<InventoryItem> itemOpt = inventoryItemRepository.findById(itemId);
            Order rejectedOrder = new Order(
                    customerOpt.orElse(null), // might be null; shows error in report/audit log
                    itemOpt.orElse(null),     // might be null
                    quantity,
                    "REJECTED");
            return orderRepository.save(rejectedOrder);
        }

        // Try to fetch customer and item from the DB
        Optional<Customer> customerOpt = customerRepository.findById(customerId);
        Optional<InventoryItem> itemOpt = inventoryItemRepository.findById(itemId);

        if (customerOpt.isEmpty() || itemOpt.isEmpty()) {
            // Either the customer or the inventory item does not exist—reject order!
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
            // Sufficient stock, so fulfill order and decrease inventory
            inventoryService.decreaseStock(itemId, quantity);
            status = "PLACED";
        } else {
            // Not enough stock: order can't be fulfilled, so reject
            status = "REJECTED";
        }

        // Always record successful or rejected order for full audit trail
        Order order = new Order(customerOpt.get(), item, quantity, status);
        return orderRepository.save(order);
    }

    /**
     * Get all orders for a specific customer.
     *
     * @param customerId customer’s ID
     * @return List of all orders for that customer
     * 
     * Why use repository method name like findByCustomerId?
     * - Lets you easily filter orders for just one customer, leveraging Spring Data's method-query magic.
     * - In production, consider adding pagination, filtering by status/date, etc.
     */
    public List<Order> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}
