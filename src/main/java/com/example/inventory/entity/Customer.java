package com.example.inventory.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Customer entity: represents each customer in your database.
 * 
 * Why use @Entity?
 * - Marks this class so that JPA/Hibernate knows it should map it to a table in your database.
 *
 * What does @Table(name = "customers") do?
 * - Optional: Explicitly sets the name of the table in your DB (otherwise it'd use the class name by default).
 * 
 * What to care about:
 * - Must have a no-argument constructor for JPA. (It's required to instantiate entities via reflection.)
 * - Fields should be private, and access should be via getters/setters to follow JavaBeans standards (Spring expects this).
 */
@Entity
@Table(name = "customers")
public class Customer {

    /**
     * The unique primary key for each customer.
     *
     * @Id - declares this field as the primary key.
     * @GeneratedValue - tells JPA to generate this value automatically.
     *   - strategy = GenerationType.IDENTITY means DB will auto-increment the value.
     *
     * Why? Let's the DB, not you, decide what the next Id should be!
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the customer.
     * 
     * - By default, this gets mapped to a column "name" in the table.
     * - You can use @Column(name = "...") if you want to customize column names.
     */
    private String name;

    /**
     * Default constructor REQUIRED by JPA.
     * - Always include a public no-args constructor in entity classes!
     */
    public Customer() {
    }

    /**
     * Custom constructor for easier object creation (without ID, which DB generates).
     */
    public Customer(String name) {
        this.name = name;
    }

    /**
     * Getter for the id field.
     * Important: No setter for id because we want the database to control that field.
     * 
     * @return database-generated unique identifier for this customer
     */
    public Long getId() {
        return id;
    }

    /**
     * Getter for the customer's name.
     * 
     * @return The name of the customer
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the customer's name.
     * 
     * @param name The new name for the customer
     */
    public void setName(String name) {
        this.name = name;
    }
}
