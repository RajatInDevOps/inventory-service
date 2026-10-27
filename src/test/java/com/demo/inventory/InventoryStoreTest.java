package com.demo.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryStoreTest {

    private InventoryStore store;

    @BeforeEach
    void setUp() {
        store = new InventoryStore();
    }

    @Test
    void seedsThreeItems() {
        assertEquals(3, store.size());
    }

    @Test
    void reservingTakesStockOut() {
        int before = store.find(1L).orElseThrow().getQuantity();
        assertEquals(before - 5, store.reserve(1L, 5).orElseThrow().getQuantity());
    }

    @Test
    void willNotReserveMoreThanItHas() {
        assertTrue(store.reserve(3L, 1).isEmpty());   // seeded with 0
        assertTrue(store.reserve(2L, 99).isEmpty());
    }

    @Test
    void refusesANonsenseAmount() {
        assertTrue(store.reserve(1L, 0).isEmpty());
        assertTrue(store.reserve(1L, -3).isEmpty());
        assertTrue(store.restock(1L, 0).isEmpty());
    }

    @Test
    void anUnknownItemIsNotFound() {
        assertFalse(store.find(4242L).isPresent());
        assertTrue(store.reserve(4242L, 1).isEmpty());
    }

    @Test
    void restockingAddsToStock() {
        assertEquals(10, store.restock(3L, 10).orElseThrow().getQuantity());
    }

    @Test
    void lowStockUsesTheThreshold() {
        assertEquals(1, store.lowStock(1).size());    // only the one at 0
        assertEquals(2, store.lowStock(10).size());   // 0 and 8
    }
}
