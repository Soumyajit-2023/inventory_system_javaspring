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

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @InjectMocks
    private InventoryService inventoryService;

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

    @Test
    void testGetItemById_found() {
        InventoryItem item = new InventoryItem();
        item.setName("Table");
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));

        Optional<InventoryItem> result = inventoryService.getItemById(1L);
        assertTrue(result.isPresent());
        assertEquals("Table", result.get().getName());
    }

    @Test
    void testGetItemById_notFound() {
        when(inventoryItemRepository.findById(100L)).thenReturn(Optional.empty());
        Optional<InventoryItem> result = inventoryService.getItemById(100L);
        assertFalse(result.isPresent());
    }

    @Test
    void testSaveItem_savesAndReturnsEntity() {
        InventoryItem item = new InventoryItem();
        item.setName("Chair");
        when(inventoryItemRepository.save(any(InventoryItem.class))).thenReturn(item);

        InventoryItem saved = inventoryService.saveItem(item);
        assertEquals("Chair", saved.getName());
        verify(inventoryItemRepository).save(item);
    }

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

    @Test
    void testDeleteItemById_callsRepository() {
        inventoryService.deleteItemById(10L);
        verify(inventoryItemRepository).deleteById(10L);
    }

    // Additional tests for comprehensive coverage (Python parity)

    @Test
    void testAddProductWithNegativeQuantity_shouldFail() {
        // TODO: Add logic
    }

    @Test
    void testAddProductWithDuplicateId_increasesQuantity() {
        // TODO: Add logic
    }

    @Test
    void testUpdateStockForNonExistentProduct_shouldFail() {
        // TODO: Add logic
    }

    @Test
    void testReduceStockInsufficient_shouldFail() {
        // TODO: Add logic
    }

    @Test
    void testAddProductWithZeroQuantity() {
        // TODO: Add logic
    }

    @Test
    void testCaseSensitivityInProductId() {
        // TODO: Add logic
    }

    @Test
    void testAddProductWithEmptyId_shouldFail() {
        // TODO: Add logic
    }

    @Test
    void testBulkAddAndRemoveProducts() {
        // TODO: Add logic
    }

    @Test
    void testReduceToZeroStockViaMultipleReduces() {
        // TODO: Add logic
    }

    @Test
    void testGetStockForNonExistentProduct_returnsZero() {
        // TODO: Add logic
    }

    @Test
    void testSpecialCharacterProductId() {
        // TODO: Add logic
    }

    @Test
    void testBulkCrossProductOperations() {
        // TODO: Add logic
    }

    @Test
    void testAddProductWithStringAsQuantity_shouldFail() {
        // TODO: Add logic
    }

    @Test
    void testNonStringProductNameAsId_shouldFail() {
        // TODO: Add logic
    }

    @Test
    void testSystemRecoversFromAllZeroStock() {
        // TODO: Add logic
    }
}
