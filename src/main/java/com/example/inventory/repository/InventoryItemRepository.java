package com.example.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.inventory.entity.InventoryItem;

/**
 * The repository interface for InventoryItem entity.
 *
 * What does a repository do?
 * - Serves as the "data access" layer, giving you methods to query and modify your database.
 * - By extending JpaRepository, Spring provides ready-made methods for saving, updating, deleting, and finding inventory items—no SQL required!
 *
 * Why extend JpaRepository<InventoryItem, Long>?
 * - <InventoryItem, Long>: Handles InventoryItem entities, with a primary key of type Long.
 * - You get inherited methods like: save(), findAll(), findById(), deleteById(), etc.
 *
 * Why interface (not class)?
 * - Spring Data JPA creates the actual class for you behind the scenes.
 *
 * How can you customize?
 * - Add your own finder methods; for example, List<InventoryItem> findByName(String name);
 *   Spring will magically generate the query for you if you follow the naming convention.
 */
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
}
