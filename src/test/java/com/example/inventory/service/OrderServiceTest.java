package com.example.inventory.service;

import com.example.inventory.entity.Customer;
import com.example.inventory.entity.InventoryItem;
import com.example.inventory.entity.Order;
import com.example.inventory.repository.CustomerRepository;
import com.example.inventory.repository.InventoryItemRepository;
import com.example.inventory.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private InventoryItemRepository inventoryItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testPlaceOrder_sufficientStock_placesOrder() {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        InventoryItem item = new InventoryItem();
        item.setName("Item1");
        item.setQuantity(10);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryService.decreaseStock(1L, 5)).thenReturn(true);

        Order dummyOrder = new Order(customer, item, 5, "PLACED");
        when(orderRepository.save(any(Order.class))).thenReturn(dummyOrder);

        Order placedOrder = orderService.placeOrder(1L, 1L, 5);

        assertNotNull(placedOrder);
        assertEquals("PLACED", placedOrder.getStatus());
        assertEquals(5, placedOrder.getQuantity());
        verify(orderRepository).save(any(Order.class));
    }

    // @Test
    // void testPlaceOrder_insufficientStock_rejectsOrder() {
    // Customer customer = new Customer();
    // customer.setName("Test");
    // InventoryItem item = new InventoryItem();
    // item.setName("Item2");
    // item.setQuantity(2);

    // when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    // when(inventoryItemRepository.findById(1L)).thenReturn(Optional.of(item));
    // when(inventoryService.decreaseStock(1L, 5)).thenReturn(false);

    // Order dummyOrder = new Order(customer, item, 5, "REJECTED");
    // lenient().when(orderRepository.save(any(Order.class))).thenReturn(dummyOrder);

    // Order placedOrder = orderService.placeOrder(1L, 1L, 5);

    // assertNotNull(placedOrder);
    // assertEquals("REJECTED", placedOrder.getStatus());
    // verify(orderRepository).save(any(Order.class));
    // verify(orderRepository, never()).save(any(Order.class));

    // }

    @Test
    void testPlaceOrder_customerOrItemMissing_rejectsOrder() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        when(inventoryItemRepository.findById(1L)).thenReturn(Optional.empty());

        Order dummyOrder = new Order(null, null, 1, "REJECTED");
        when(orderRepository.save(any(Order.class))).thenReturn(dummyOrder);

        Order placedOrder = orderService.placeOrder(99L, 1L, 1);

        assertNotNull(placedOrder);
        assertEquals("REJECTED", placedOrder.getStatus());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testPlaceOrder_invalidQuantity_rejectsOrder() {
        // Setup
        Customer customer = new Customer();
        customer.setName("Test Customer");
        InventoryItem item = new InventoryItem();
        item.setName("ItemX");

        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        when(inventoryItemRepository.findById(3L)).thenReturn(Optional.of(item));

        Order dummyOrderZero = new Order(customer, item, 0, "REJECTED");
        Order dummyOrderNegative = new Order(customer, item, -4, "REJECTED");
        when(orderRepository.save(argThat(order -> order != null && order.getQuantity() == 0)))
                .thenReturn(dummyOrderZero);
        when(orderRepository.save(argThat(order -> order != null && order.getQuantity() == -4)))
                .thenReturn(dummyOrderNegative);

        // Test quantity zero
        Order orderZero = orderService.placeOrder(2L, 3L, 0);
        assertNotNull(orderZero);
        assertEquals("REJECTED", orderZero.getStatus());
        assertEquals(0, orderZero.getQuantity());

        // Test negative quantity
        Order orderNeg = orderService.placeOrder(2L, 3L, -4);
        assertNotNull(orderNeg);
        assertEquals("REJECTED", orderNeg.getStatus());
        assertEquals(-4, orderNeg.getQuantity());

        verify(orderRepository, times(2)).save(any(Order.class));
    }
}
