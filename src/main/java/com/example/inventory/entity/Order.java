package com.example.inventory.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private InventoryItem item;

    private int quantity;

    private String status; // "PLACED" or "REJECTED"

    public Order() {
    }

    public Order(Customer customer, InventoryItem item, int quantity, String status) {
        this.customer = customer;
        this.item = item;
        this.quantity = quantity;
        this.status = status;
    }

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
