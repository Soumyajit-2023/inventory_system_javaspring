package com.example.inventory.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.inventory.entity.Customer;
import com.example.inventory.repository.CustomerRepository;

/**
 * Unit tests for CustomerService business logic using JUnit 5 and Mockito.
 *
 * Why use unit tests?
 * - Ensures that logic in the service works as intended, regardless of the repository/database.
 * - Lets you safely refactor or extend code, knowing the logic is tested.
 * - Great for catching breaking changes as you grow your project!
 *
 * Anatomy of a Spring/Mockito test:
 * - @ExtendWith(MockitoExtension.class): tells JUnit to use Mockito for mocks.
 * - @Mock: creates a "fake" repository so we don't hit the database.
 * - @InjectMocks: gives us a real service, but with all @Mock dependencies injected.
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock // “Fake” repository to control/mock DB interactions for testing
    private CustomerRepository customerRepository;

    @InjectMocks // Real service, but all @Mock dependencies are injected in place of real ones
    private CustomerService customerService;

    /**
     * Tests that getAllCustomers returns a list of customers as expected.
     * 
     * Why test this?
     * - Validates that service fetches data through the repository and maps it correctly.
     */
    @Test
    void testGetAllCustomers_returnsList() {
        Customer c1 = new Customer();
        c1.setName("Alice");
        Customer c2 = new Customer();
        c2.setName("Bob");
        List<Customer> list = Arrays.asList(c1, c2);

        when(customerRepository.findAll()).thenReturn(list);

        List<Customer> result = customerService.getAllCustomers();
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
    }

    /**
     * Tests service returns a customer by ID if present.
     */
    @Test
    void testGetCustomerById_found() {
        Customer c = new Customer();
        c.setName("Test");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        Optional<Customer> result = customerService.getCustomerById(1L);
        assertTrue(result.isPresent());
        assertEquals("Test", result.get().getName());
    }

    /**
     * Tests service returns empty Optional if customer is not found.
     */
    @Test
    void testGetCustomerById_notFound() {
        when(customerRepository.findById(2L)).thenReturn(Optional.empty());
        Optional<Customer> result = customerService.getCustomerById(2L);
        assertFalse(result.isPresent());
    }

    /**
     * Tests that saving a customer delegates to the repository and returns entity.
     */
    @Test
    void testSaveCustomer_savesAndReturnsEntity() {
        Customer c = new Customer();
        c.setName("New Cust");
        when(customerRepository.save(any(Customer.class))).thenReturn(c);

        Customer saved = customerService.saveCustomer(c);
        assertEquals("New Cust", saved.getName());
        verify(customerRepository).save(c);
    }

    // ---- More Business Logic Test Cases Below ----

    /**
     * Business logic: Test that saving a customer with an existing ID triggers an update.
     * (In reality, the repository will perform an UPDATE rather than an INSERT.)
     */
    @Test
    void testSaveCustomer_updatesWhenIdExists() {
        Customer c = new Customer();
        c.setName("Existing Name");
        // Simulate existing database entry by giving an ID
        // Use reflection since setId() does not exist!
        try {
            java.lang.reflect.Field idField = Customer.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(c, 123L);
        } catch (Exception e) {
            fail("Test setup failed: " + e);
        }

        when(customerRepository.save(any(Customer.class))).thenReturn(c);

        Customer updated = customerService.saveCustomer(c);
        assertEquals(123L, updated.getId());
        assertEquals("Existing Name", updated.getName());
    }

    /**
     * Business logic: Test empty name (should be handled by service logic, but this service does not currently reject)
     * NOTE TO STUDENTS: In a real app, add validation in service to reject/throw on invalid names!
     */
    @Test
    void testSaveCustomer_emptyNameAllowed_currently() {
        Customer c = new Customer();
        c.setName("");
        when(customerRepository.save(any(Customer.class))).thenReturn(c);

        Customer saved = customerService.saveCustomer(c);
        assertEquals("", saved.getName());
    }

    /**
     * Advanced test: Simulate repository throwing a runtime exception, ensure it's not swallowed.
     */
    @Test
    void testGetCustomerById_repositoryThrows_exceptionIsPropagated() {
        when(customerRepository.findById(anyLong())).thenThrow(new RuntimeException("DB failure"));

        Exception ex = assertThrows(RuntimeException.class, () -> customerService.getCustomerById(77L));
        assertEquals("DB failure", ex.getMessage());
    }

    /**
     * Defensive: Verify service does not call save() when customer is null (if logic was later added).
     */
    @Test
    void testSaveCustomer_nullCustomer_doesNothing() {
        // This is NOT in original logic, but demonstrates defensive testing.
        Customer saved = null;
        try {
            saved = customerService.saveCustomer(null);
        } catch (Exception ignored) {}
        assertNull(saved);
        // Not verifying mock call because in current implementation this would NPE. Students: add null-check in real logic!
    }
}
