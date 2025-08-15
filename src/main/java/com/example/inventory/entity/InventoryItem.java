package com.example.inventory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * InventoryItem entity: represents an item in your inventory database table.
 * 
 * Why use @Entity and @Table?
 * - @Entity tells JPA to map this Java class to a DB table.
 * - @Table(name = "inventory-item") lets you pick a specific table name (with dash).
 *     - If you used @Table without name, table would default to "inventoryitem".
 * 
 * What should a beginner take care about?
 * - Always have a default (no-arg) constructor.
 * - All entity fields (columns) should be private and accessed with getters/setters (JavaBeans standard).
 * - Single responsibility: don't put extra logic here, just data structure.
 */
@Entity
@Table(name = "inventory-item")
public class InventoryItem {

    /**
     * Primary key (ID) for this inventory item.
     * 
     * - @Id: Marks this as the unique identifier for each InventoryItem row.
     * - @GeneratedValue(strategy = GenerationType.IDENTITY): DB will auto-increment the id for new rows.
     *     - Always let the DB handle unique IDs!
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the inventory item.
     */
    private String name;

    /**
     * Quantity of this item available in stock.
     */
    private int quantity;

    /**
     * Default constructor needed by JPA.
     */
    public InventoryItem() {
    }

    /**
     * Custom constructor when creating new InventoryItems in your app logic (not used by JPA).
     */
    public InventoryItem(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    /**
     * Getter for id (no setter to ensure ID only set by DB).
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter and setter for name.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter and setter for quantity.
     * Always validate quantity >= 0 in your application logic to avoid negative stock numbers.
     */
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
