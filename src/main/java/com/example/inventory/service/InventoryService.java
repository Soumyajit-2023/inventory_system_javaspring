package com.example.inventory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.inventory.entity.InventoryItem;
import com.example.inventory.repository.InventoryItemRepository;

/**
 * InventoryService: Holds business logic for handling inventory management.
 * 
 * Why do we need a Service layer?
 * - Separates your "rules and logic" from your "web API" (controller) and "data access" (repository).
 * - Makes your code modular, easier to test, and more maintainable.
 * 
 * Best practice: Put all validation, checks, and business flows here.
 */
@Service
public class InventoryService {

    /**
     * Dependency injection for the repository.
     * - @Autowired: Spring supplies the InventoryItemRepository instance automatically.
     * - Promotes "loose coupling" (you can replace repo in tests if needed!).
     */
    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    /**
     * Lists all inventory items.
     * 
     * What to take care:
     * - For very large inventories, consider pagination/filtering in real apps.
     * 
     * @return List of all InventoryItems in the DB.
     */
    public List<InventoryItem> getAllItems() {
        return inventoryItemRepository.findAll();
    }

    /**
     * Retrieves a single inventory item by its ID (if present).
     * 
     * @param id unique identifier of the item
     * @return Optional wrapping the InventoryItem if it exists
     * 
     * What to take care:
     * - Always check isPresent() before accessing the item!
     */
    public Optional<InventoryItem> getItemById(Long id) {
        return inventoryItemRepository.findById(id);
    }

    /**
     * Creates or updates an inventory item.
     * 
     * @param item inventory item to save
     * @return the saved InventoryItem (with updated fields/ID!)
     * 
     * What to take care:
     * - Always validate item data (e.g., name not empty, quantity not negative) in production logic.
     */
    public InventoryItem saveItem(InventoryItem item) {
        return inventoryItemRepository.save(item);
    }

    /**
     * Decreases the stock of an inventory item by a given quantity.
     * 
     * Why this method?
     * - Encapsulates business logic for only allowing a decrease if enough stock is available.
     * 
     * @param itemId the inventory item's ID
     * @param quantity the amount to decrease
     * @return true if decrease succeeded, false if not enough stock or item not found.
     * 
     * Step-by-step logic for beginners:
     * 1. Find the item by ID.
     * 2. If not present, return false (can't decrease unavailable item).
     * 3. If present, check if current quantity is at least as much as requested.
     *    - If yes, subtract quantity and save updated item, then return true.
     *    - If no, return false (not enough stock).
     * 
     * What to take care:
     * - Always guard against negative quantities!
     * - In real-world, handle concurrency (two users trying to buy last stock at same time).
     */
    public boolean decreaseStock(Long itemId, int quantity) {
        Optional<InventoryItem> itemOpt = inventoryItemRepository.findById(itemId);
        if (itemOpt.isPresent()) {
            InventoryItem item = itemOpt.get();
            if (item.getQuantity() >= quantity) {
                item.setQuantity(item.getQuantity() - quantity);
                inventoryItemRepository.save(item);
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes an inventory item by its ID.
     * 
     * @param id the item's ID
     * 
     * What to take care:
     * - Always check if the item exists before deleting for critical systems.
     * - May want to "soft delete" (mark as inactive rather than permanently remove) in production.
     */
    public void deleteItemById(Long id) {
        inventoryItemRepository.deleteById(id);
    }
}
