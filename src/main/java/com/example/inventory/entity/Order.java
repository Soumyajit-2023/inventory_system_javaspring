package com.example.inventory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Order entity: represents a single order in your system.
 * 
 * Why use @Entity and @Table?
 * - @Entity tells JPA to map this class to a DB table.
 * - @Table(name = "orders") explicitly sets the table name as "orders" (often a good idea)
 * 
 * Why use references, not IDs?
 * - JPA sets up relationships using objects (see Customer/InventoryItem fields) instead of just storing their IDs,
 *   allowing easy navigation/order.customer.getName() in code.
 * 
 * What should a beginner take care of?
 * - Always have a default constructor.
 * - Each entity’s fields should be private, with public getters/setters.
 * - In production, be careful with circular references (e.g., Customer with List<Order> can cause stack overflow in JSON serialization).
 */
@Entity
@Table(name = "orders")
public class Order {
    /**
     * The unique ID for this order
     * @Id marks primary key
     * @GeneratedValue lets the DB handle ID creation
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The customer who made this order.
     *
     * @ManyToOne: Many orders can belong to one customer.
     * @JoinColumn: Name of column in "orders" table that stores the customer foreign key.
     *   (This lets you use order.getCustomer().getName() etc directly in code.)
     */
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    /**
     * The item being ordered.
     * 
     * @ManyToOne: Many orders may reference the same item.
     * @JoinColumn: Sets the DB column for the item relationship.
     */
    @ManyToOne
    @JoinColumn(name = "item_id")
    private InventoryItem item;

    /**
     * The quantity ordered for this item.
     * Always validate quantity > 0 in your app’s logic!
     */
    private int quantity;

    /**
     * The order status: for example, "PLACED" or "REJECTED"
     * In a real-world app, this would likely be an enum type for safety.
     */
    private String status; // "PLACED" or "REJECTED"

    /**
     * Default constructor REQUIRED by JPA.
     */
    public Order() {
    }

    /**
     * Custom constructor for creating orders in your own app logic.
     * (ID is set by the database automatically.)
     */
    public Order(Customer customer, InventoryItem item, int quantity, String status) {
        this.customer = customer;
        this.item = item;
        this.quantity = quantity;
        this.status = status;
    }

    // Getters and setters for all fields. Use these for accessing/modifying object data.

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public InventoryItem getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setItem(InventoryItem item) {
        this.item = item;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
