package com.example.inventory.service;

import com.example.inventory.entity.InventoryItem;
import com.example.inventory.repository.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    public List<InventoryItem> getAllItems() {
        return inventoryItemRepository.findAll();
    }

    public Optional<InventoryItem> getItemById(Long id) {
        return inventoryItemRepository.findById(id);
    }

    public InventoryItem saveItem(InventoryItem item) {
        return inventoryItemRepository.save(item);
    }

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

    public void deleteItemById(Long id) {
        inventoryItemRepository.deleteById(id);
    }
}
