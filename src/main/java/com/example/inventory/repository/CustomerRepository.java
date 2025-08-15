package com.example.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventory.entity.Customer;

/**
 * The repository interface for Customer entity.
 * 
 * What is a repository?
 * - "Data access layer": defines how you communicate with the database.
 * - Spring Data JPA scans for interfaces extending JpaRepository and automatically provides implementations at runtime!
 * - You don't need to write SQL for the most common CRUD operations.
 * 
 * Why extend JpaRepository<Customer, Long>?
 * - <Customer, Long>: This repository manages Customer entities and the primary key is of type Long.
 * - JpaRepository gives you:
 *   - save(), findAll(), findById(), deleteById(), and many more methods automatically!
 * 
 * Why interface (not class)?
 * - Spring will generate a working class behind the scenes with all database operations for you.
 * 
 * You can add your own custom queries here too:
 *   - Example: List<Customer> findByName(String name);
 *     (Spring will auto-implement these patterns for you as well!)
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
