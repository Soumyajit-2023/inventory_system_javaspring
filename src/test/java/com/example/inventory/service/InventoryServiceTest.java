package com.example.inventory.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.inventory.entity.InventoryItem;
import com.example.inventory.repository.InventoryItemRepository;

/**
 * Unit tests for InventoryService using JUnit and Mockito.
 * 
 * Why do we extensively test here?
 * - Validates business rules and error cases beyond just repository CRUD.
 * - Ensures inventory service correctly tracks and updates quantities through all code paths!
 * - All methods and business boundaries are tested—great for learning test-driven development.
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @InjectMocks
    private InventoryService inventoryService;

    /**
     * Tests that service returns all inventory items provided by repository.
     */
    @Test
    void testGetAllItems_returnsList() {
        InventoryItem i1 = new InventoryItem();
        i1.setName("Pen");
        InventoryItem i2 = new InventoryItem();
        i2.setName("Book");
        List<InventoryItem> items = Arrays.asList(i1, i2);

        when(inventoryItemRepository.findAll()).thenReturn(items);

        List<InventoryItem> result = inventoryService.getAllItems();
        assertEquals(2, result.size());
        assertEquals("Pen", result.get(0).getName());
    }

    /**
     * Tests service returns item by ID if exists.
     */
    @Test
    void testGetItemById_found() {
        InventoryItem item = new InventoryItem();
        item.setName("Table");
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));

        Optional<InventoryItem> result = inventoryService.getItemById(1L);
        assertTrue(result.isPresent());
        assertEquals("Table", result.get().getName());
    }

    /**
     * Tests service returns empty when item is not found.
     */
    @Test
    void testGetItemById_notFound() {
        when(inventoryItemRepository.findById(100L)).thenReturn(Optional.empty());
        Optional<InventoryItem> result = inventoryService.getItemById(100L);
        assertFalse(result.isPresent());
    }

    /**
     * Tests that saveItem persists entity and passes it through.
     */
    @Test
    void testSaveItem_savesAndReturnsEntity() {
        InventoryItem item = new InventoryItem();
        item.setName("Chair");
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(item);

        InventoryItem saved = inventoryService.saveItem(item);
        assertEquals("Chair", saved.getName());
        verify(inventoryItemRepository).save(item);
    }

    /**
     * Tests that stock is decreased if enough is available.
     */
    @Test
    void testDecreaseStock_sufficientStock() {
        InventoryItem item = new InventoryItem();
        item.setName("Notes");
        item.setQuantity(10);

        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(item);

        boolean result = inventoryService.decreaseStock(1L, 5);

        assertTrue(result);
        assertEquals(5, item.getQuantity());
    }

    /**
     * Tests that decrease fails if not enough stock.
     */
    @Test
    void testDecreaseStock_insufficientStock() {
        InventoryItem item = new InventoryItem();
        item.setQuantity(3);

        when(inventoryItemRepository.findById(2L)).thenReturn(Optional.of(item));

        boolean result = inventoryService.decreaseStock(2L, 5);

        assertFalse(result);
        assertEquals(3, item.getQuantity());
        verify(inventoryItemRepository, never()).save(any());
    }

    /**
     * Tests deleteItemById delegates to repository.
     */
    @Test
    void testDeleteItemById_callsRepository() {
        inventoryService.deleteItemById(10L);
        verify(inventoryItemRepository).deleteById(10L);
    }

    // ------- Additional Business Logic Test Cases Below with Explanations -------

    /**
     * Edge case: Decreasing stock on non-existent item should return false, never call save.
     */
    @Test
    void testDecreaseStock_nonExistentItem() {
        when(inventoryItemRepository.findById(111L)).thenReturn(Optional.empty());
        boolean result = inventoryService.decreaseStock(111L, 1);
        assertFalse(result);
        verify(inventoryItemRepository, never()).save(any());
    }

    /**
     * Edge case: Trying to decrease by negative quantity should NOT change the stock.
     * (Current logic does not handle this, but a real app should block it!)
     */
    @Test
    void testDecreaseStock_negativeQuantity() {
        InventoryItem item = new InventoryItem();
        item.setQuantity(10);
        when(inventoryItemRepository.findById(7L)).thenReturn(Optional.of(item));

        // should ideally return false (and not allow reducing by negative!), but current logic doesn't block:
        boolean result = inventoryService.decreaseStock(7L, -5);
        // Depending on business rule, this may pass (makes quantity 15!) or should fail.
        // We assert what current code does; for teaching, note that validation is missing here.
        assertTrue(result);
        assertEquals(15, item.getQuantity(), 
            "BUG: Negative quantity should not be allowed. Business logic should have a check!");
    }

    /**
     * Business: Saving an item with zero quantity.
     * (Currently allowed, may want to block in real app.)
     */
    @Test
    void testSaveItem_zeroQuantityAllowedCurrently() {
        InventoryItem item = new InventoryItem();
        item.setName("ZeroStock");
        item.setQuantity(0);
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(item);

        InventoryItem saved = inventoryService.saveItem(item);
        assertEquals(0, saved.getQuantity());
    }

    /**
     * Business: Reduce stock multiple times until it reaches exactly zero (no negative allowed).
     */
    @Test
    void testMultipleDecreases_reachesZeroStock() {
        InventoryItem item = new InventoryItem();
        item.setQuantity(10);
        when(inventoryItemRepository.findById(8L)).thenReturn(Optional.of(item));
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(item);

        assertTrue(inventoryService.decreaseStock(8L, 5));
        assertEquals(5, item.getQuantity());
        assertTrue(inventoryService.decreaseStock(8L, 5));
        assertEquals(0, item.getQuantity());
        // Now further decrease should fail
        assertFalse(inventoryService.decreaseStock(8L, 1));
        assertEquals(0, item.getQuantity());
    }
}
