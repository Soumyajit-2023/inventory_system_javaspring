package com.example.inventory.controller;

import com.example.inventory.entity.Customer;
import com.example.inventory.entity.InventoryItem;
import com.example.inventory.repository.CustomerRepository;
import com.example.inventory.repository.InventoryItemRepository;
import com.example.inventory.repository.OrderRepository;
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
class OrderControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private CustomerRepository customerRepository;
        @Autowired
        private InventoryItemRepository inventoryItemRepository;
        @Autowired
        private OrderRepository orderRepository;

        private Customer customer;
        private InventoryItem item;

        @BeforeEach
        void setUp() {
                orderRepository.deleteAll();
                inventoryItemRepository.deleteAll();
                customerRepository.deleteAll();

                customer = new Customer();
                customer.setName("OrderTest User");
                customer = customerRepository.save(customer);

                item = new InventoryItem();
                item.setName("Widget");
                item.setQuantity(10);
                item = inventoryItemRepository.save(item);
        }

        @Test
        void testPostOrder_validRequest_stockReduced_statusPlaced() throws Exception {
                String body = String.format(
                                "{\"customerId\":%d,\"itemId\":%d,\"quantity\":3}",
                                customer.getId(), item.getId());

                mockMvc.perform(post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("PLACED"))
                                .andExpect(jsonPath("$.quantity").value(3));

                InventoryItem updated = inventoryItemRepository.findById(item.getId()).get();
                assertEquals(7, updated.getQuantity());
        }

        @Test
        void testPostOrder_insufficientStock_statusRejected() throws Exception {
                String body = String.format(
                                "{\"customerId\":%d,\"itemId\":%d,\"quantity\":100}",
                                customer.getId(), item.getId());

                mockMvc.perform(post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("REJECTED"));

                InventoryItem unchanged = inventoryItemRepository.findById(item.getId()).get();
                assertEquals(10, unchanged.getQuantity());
        }

        @Test
        void testPostOrder_customerMissing_statusRejected() throws Exception {
                String body = String.format(
                                "{\"customerId\":%d,\"itemId\":%d,\"quantity\":2}",
                                9999, item.getId());

                mockMvc.perform(post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("REJECTED"));
        }

        @Test
        void testPostOrder_itemMissing_statusRejected() throws Exception {
                String body = String.format(
                                "{\"customerId\":%d,\"itemId\":%d,\"quantity\":2}",
                                customer.getId(), 99999);

                mockMvc.perform(post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(body))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("REJECTED"));
        }

        @Test
        void testGetOrdersByCustomer_returnsAllOrders() throws Exception {
                // Place two orders for the customer to generate data
                String body1 = String.format(
                                "{\"customerId\":%d,\"itemId\":%d,\"quantity\":2}",
                                customer.getId(), item.getId());
                String body2 = String.format(
                                "{\"customerId\":%d,\"itemId\":%d,\"quantity\":5}",
                                customer.getId(), item.getId());

                mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(body1));
                mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(body2));

                mockMvc.perform(get("/orders/" + customer.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[*].quantity", everyItem(greaterThan(0))));
        }

        @Test
        void testPostOrder_invalidQuantity_statusRejected() throws Exception {
                // zero quantity
                String bodyZero = String.format(
                                "{\"customerId\":%d,\"itemId\":%d,\"quantity\":0}",
                                customer.getId(), item.getId());
                mockMvc.perform(post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bodyZero))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("REJECTED"));

                // negative quantity
                String bodyNeg = String.format(
                                "{\"customerId\":%d,\"itemId\":%d,\"quantity\":-5}",
                                customer.getId(), item.getId());
                mockMvc.perform(post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bodyNeg))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("REJECTED"));
        }

        @Test
        void testOrderLog_containsRejectedForInvalidQuantities() throws Exception {
                // Submit orders with qty zero and negative
                String bodyZero = String.format(
                                "{\"customerId\":%d,\"itemId\":%d,\"quantity\":0}",
                                customer.getId(), item.getId());
                String bodyNeg = String.format(
                                "{\"customerId\":%d,\"itemId\":%d,\"quantity\":-11}",
                                customer.getId(), item.getId());

                mockMvc.perform(post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bodyZero));

                mockMvc.perform(post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(bodyNeg));

                // Check order logs (database)
                var orders = orderRepository.findAll();
                assertTrue(orders.stream()
                                .filter(o -> o.getQuantity() <= 0)
                                .allMatch(o -> "REJECTED".equals(o.getStatus())));
        }
}
