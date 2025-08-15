package com.example.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventory.entity.Order;

/**
 * The repository interface for Order entity.
 * 
 * What does this provide?
 * - Spring Data JPA gives you ready-to-use methods for saving, finding, deleting, and updating orders—no SQL needed!
 * 
 * Why extend JpaRepository<Order, Long>?
 * - Handles Order objects with primary key type Long.
 * - JpaRepository gives save(), findAll(), findById(), deleteById(), etc.
 * 
 * Why add custom finder methods?
 * - Method naming pattern lets you write methods like findByCustomerId(Long customerId) and Spring will auto-generate the query.
 * - Makes code much more readable and less error-prone than hand-writing SQL for basic needs!
 * 
 * Example: 
 * - List<Order> findByCustomerId(Long customerId)
 *   This fetches all orders that belong to a specific customer, based on their ID.
 *   (Spring will automatically generate the query behind the scenes based on the method name.)
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerId(Long customerId);
}
