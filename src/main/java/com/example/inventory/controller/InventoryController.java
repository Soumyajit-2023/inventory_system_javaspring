package com.example.inventory.controller;

import java.util.List; // Our data type for inventory entries (like an object representing a product)

import org.springframework.beans.factory.annotation.Autowired; // Business logic for inventory lives here
import org.springframework.web.bind.annotation.DeleteMapping; // Lets us auto-inject the service into our controller
import org.springframework.web.bind.annotation.GetMapping; // RESTful controller and endpoint annotations
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.inventory.entity.InventoryItem;
import com.example.inventory.service.InventoryService;

/**
 * InventoryController manages all HTTP requests related to Inventory management (viewing, adding, deleting items).
 *
 * Why use this class?
 * - This is where the backend receives API calls related to inventory and passes them to the service (business logic) layer.
 * - Separates "web/API" code from "business rule" code!
 *
 * Key Spring REST terms explained:
 * - @RestController: Marks this as a controller whose return values will be automatically JSON-ified.
 * - @RequestMapping("/inventory"): Every route here starts with /inventory.
 */
@RestController
@RequestMapping("/inventory") // All endpoints in this controller will be under /inventory
public class InventoryController {

    @Autowired // Auto-injects (wires) the correct InventoryService bean for us
    private InventoryService inventoryService;

    /**
     * Handles GET requests for listing all inventory items.
     * Example: GET /inventory
     * 
     * @return list of all items in inventory as JSON.
     * 
     * Things to care about:
     * - Returns all inventory items in the system.
     * - For large inventories, consider implementing pagination!
     */
    @GetMapping
    public List<InventoryItem> getAllItems() {
        return inventoryService.getAllItems(); // Let the service fetch all inventory data
    }

    /**
     * Handles POST requests to add a new inventory item.
     * Example: POST /inventory with item details in JSON body
     * 
     * Why @RequestBody? Converts JSON in the HTTP request to InventoryItem object.
     * 
     * Careful: In real production, add validation so we don’t get incomplete or invalid items!
     */
    @PostMapping
    public InventoryItem addItem(@RequestBody InventoryItem item) {
        return inventoryService.saveItem(item); // Pass the item to the service for saving (creation)
    }

    /**
     * Handles DELETE requests to remove an inventory item by its ID.
     * Example: DELETE /inventory/123 will delete the item with id=123
     * 
     * @PathVariable extracts the "id" from the URL and supplies it to this method.
     * 
     * Careful:
     * - Should ideally return an error if item is missing (not just void!).
     * - Always check if the ID exists before deleting (add robust error-handling in production).
     */
    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        inventoryService.deleteItemById(id); // Removes the item with the given ID
    }
}
