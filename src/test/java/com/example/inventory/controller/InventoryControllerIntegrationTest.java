package com.example.inventory.controller;

import com.example.inventory.entity.InventoryItem;
import com.example.inventory.repository.InventoryItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InventoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @BeforeEach
    void setUp() {
        inventoryItemRepository.deleteAll();
    }

    @Test
    void testGetInventory_returnsAll() throws Exception {
        InventoryItem i1 = new InventoryItem();
        i1.setName("Mouse");
        i1.setQuantity(15);
        InventoryItem i2 = new InventoryItem();
        i2.setName("Keyboard");
        i2.setQuantity(24);
        inventoryItemRepository.save(i1);
        inventoryItemRepository.save(i2);

        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[?(@.name == 'Mouse')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Mouse' && @.quantity == 15)]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Keyboard')]").exists())
                .andExpect(jsonPath("$[?(@.name == 'Keyboard' && @.quantity == 24)]").exists());
    }

    @Test
    void testPostInventory_createsOrUpdates() throws Exception {
        String body = "{\"name\":\"Laptop\",\"quantity\":50}";
        mockMvc.perform(post("/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.quantity").value(50));
    }

    @Test
    void testDeleteInventory_removesItem() throws Exception {
        InventoryItem item = new InventoryItem();
        item.setName("Table");
        item.setQuantity(1);
        item = inventoryItemRepository.save(item);

        mockMvc.perform(delete("/inventory/" + item.getId()))
                .andExpect(status().isOk());

        assertFalse(inventoryItemRepository.findById(item.getId()).isPresent());
    }
}
