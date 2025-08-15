package com.example.inventory.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.inventory.entity.Customer;
import com.example.inventory.entity.InventoryItem;
import com.example.inventory.entity.Order;
import com.example.inventory.repository.CustomerRepository;
import com.example.inventory.repository.InventoryItemRepository;
import com.example.inventory.repository.OrderRepository;

/**
 * Unit tests for OrderService business logic (mocking repos/services).
 *
 * Why test so many business conditions?
 * - Orders enforce a lot of app rules: stock management, valid customer/item, quantity edge cases, etc.
 * - We want to know all corners act as designed.
 */
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

    /**
     * Normal use: order placed if all conditions met, sufficient stock.
     */
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

    /**
     * Edge case: When customer or item doesn't exist, order is REJECTED (not PLACED).
     */
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

    /**
     * Edge: If quantity is zero or less, order is always REJECTED.
     */
    @Test
    void testPlaceOrder_invalidQuantity_rejectsOrder() {
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

    // ----------- More Comprehensive Business Logic Tests -----------

    /**
     * Business: If stock is insufficient, order is REJECTED.
     */
    @Test
    void testPlaceOrder_insufficientStock_rejectsOrder() {
        Customer customer = new Customer();
        customer.setName("Low Stock");
        InventoryItem item = new InventoryItem();
        item.setName("Scarce");
        item.setQuantity(2);

        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(inventoryItemRepository.findById(10L)).thenReturn(Optional.of(item));
        // Simulate not enough stock
        Order rejected = new Order(customer, item, 5, "REJECTED");
        when(orderRepository.save(any(Order.class))).thenReturn(rejected);

        // Actually the service, as coded, will check item.getQuantity() >= quantity; not call decreaseStock if not enough
        Order placedOrder = orderService.placeOrder(10L, 10L, 5);

        assertNotNull(placedOrder);
        assertEquals("REJECTED", placedOrder.getStatus());
        assertEquals(5, placedOrder.getQuantity());
        verify(orderRepository).save(any(Order.class));
    }

    /**
     * Business: Placing an order with an inventory item that just ran out (stock dropped to 0 by another process).
     */
    /**
     * Simulates two consecutive orders for the last available stock:
     * The first order should succeed and reduce stock to 0; the second should be rejected.
     * Also verifies decreaseStock method is NOT called for the rejected order.
     */
    @Test
    void testPlaceOrder_exactZeroStock_thenRejectsFurther() {
        Customer customer = new Customer();
        customer.setName("LastMinute");

        // Two inventory item instances to simulate persisted state in DB
        InventoryItem itemWithStock = new InventoryItem();
        itemWithStock.setName("Rare");
        itemWithStock.setQuantity(1);

        InventoryItem itemOutOfStock = new InventoryItem();
        itemOutOfStock.setName("Rare");
        itemOutOfStock.setQuantity(0);

        when(customerRepository.findById(2L)).thenReturn(Optional.of(customer));
        // First call: 1 left, second call: 0 left (mimic real repo)
        when(inventoryItemRepository.findById(2L))
            .thenReturn(Optional.of(itemWithStock))   // first call
            .thenReturn(Optional.of(itemOutOfStock)); // second call

        // Generic mock: every call to save just returns the Order argument (no NPE possible)
        when(orderRepository.save(any(Order.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // decreaseStock should only be called for the first order (returns true), never for the rejected one
        when(inventoryService.decreaseStock(2L, 1))
            .thenReturn(true);

        // First order: should succeed, decreaseStock invoked, item drops to 1->0
        Order placedFirst = orderService.placeOrder(2L, 2L, 1);
        assertEquals("PLACED", placedFirst.getStatus());
        verify(inventoryService, times(1)).decreaseStock(2L, 1);

        // Second order: stock is zero, decreaseStock should NOT be called again
        Order placedSecond = orderService.placeOrder(2L, 2L, 1);
        assertEquals("REJECTED", placedSecond.getStatus());
        verify(inventoryService, times(1)).decreaseStock(2L, 1); // still 1 call only

        // Additionally, check that the rejected order was still for quantity 1, as requested (not remaining stock)
        assertEquals(1, placedFirst.getQuantity());
        assertEquals(1, placedSecond.getQuantity());
    }

    /**
     * Defensive: OrderService should not place order if all dependencies are null.
     * (May not occur, but good for coverage.)
     */
    @Test
    void testPlaceOrder_allNullDependencies() {
        OrderService orphanOrderService = new OrderService();
        // all dependencies are null
        Exception ex = null;
        try {
            orphanOrderService.placeOrder(null, null, 0);
        } catch (Exception e) {
            ex = e;
        }
        assertNotNull(ex, "Should throw due to missing dependencies.");
    }

    /**
     * Business: getOrdersByCustomer delegates to OrderRepository.
     */
    @Test
    void testGetOrdersByCustomer_returnsOrders() {
        when(orderRepository.findByCustomerId(5L)).thenReturn(java.util.Collections.emptyList());
        assertTrue(orderService.getOrdersByCustomer(5L).isEmpty());
        verify(orderRepository).findByCustomerId(5L);
    }
}
